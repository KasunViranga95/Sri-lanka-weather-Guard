package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.FloodStatus
import com.example.model.HydrographTimeRange
import com.example.model.RiverGaugeStation
import com.example.ui.components.HydrographTimeRangeSelector
import com.example.ui.components.RiverHydrographCurve
import com.example.ui.components.RiverThresholdBar
import com.example.ui.theme.AdaptiveBorderColor
import com.example.ui.theme.AdaptiveCyanAccent
import com.example.ui.theme.AdaptiveElevatedColor
import com.example.ui.theme.AdaptivePrimaryText
import com.example.ui.theme.AdaptiveSecondaryText
import com.example.ui.theme.AdaptiveSurfaceColor
import com.example.ui.theme.AdaptiveTealAccent
import com.example.ui.theme.AdvisoryYellow
import com.example.ui.theme.AlertRed
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.WarningAmber
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun RiverWaterLevelsScreen(
    stations: List<RiverGaugeStation>,
    selectedBasinFilter: String,
    searchQuery: String,
    selectedHydrographTimeRange: HydrographTimeRange,
    onBasinFilterSelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onHydrographTimeRangeSelected: (HydrographTimeRange) -> Unit,
    modifier: Modifier = Modifier
) {
    val basinOptions = listOf(
        "All Basins",
        "Kelani Ganga",
        "Kalu Ganga",
        "Gin Ganga",
        "Nilwala Ganga",
        "Mahaweli Ganga",
        "Maha Oya",
        "Attanagalu Oya",
        "Deduru Oya",
        "Bentara Ganga",
        "Walawe Ganga",
        "Kirindi Oya",
        "Menik Ganga",
        "Kumbukkan Oya",
        "Heda Oya",
        "Yan Oya",
        "Malwathu Oya"
    )

    val filteredStations = remember(stations, selectedBasinFilter, searchQuery) {
        stations.filter { station ->
            val matchesBasin = selectedBasinFilter == "All Basins" ||
                station.riverName.contains(selectedBasinFilter, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                station.stationName.contains(searchQuery, ignoreCase = true) ||
                station.riverName.contains(searchQuery, ignoreCase = true) ||
                station.basinCode.contains(searchQuery, ignoreCase = true) ||
                station.district.contains(searchQuery, ignoreCase = true) ||
                (station.tributaryOf?.contains(searchQuery, ignoreCase = true) == true) ||
                station.downstreamTowns.any { it.contains(searchQuery, ignoreCase = true) }
            matchesBasin && matchesQuery
        }.sortedByDescending { it.floodStatus.severityRank * 100 + (it.currentLevelM / it.majorFloodLevelM * 100).toInt() }
    }

    val majorCount = stations.count { it.floodStatus == FloodStatus.MAJOR_FLOOD }
    val minorCount = stations.count { it.floodStatus == FloodStatus.MINOR_FLOOD }
    val alertCount = stations.count { it.floodStatus == FloodStatus.ALERT }
    val normalCount = stations.count { it.floodStatus == FloodStatus.NORMAL }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("river_levels_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Island-Wide Summary Counters (Clean 2x2 Grid so all labels fit clearly)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flood,
                        contentDescription = null,
                        tint = AdaptiveCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "RIVERWATCH.LK • IRRIGATION HYDROLOGY NETWORK",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveCyanAccent
                    )
                }
                Text(
                    text = "Sri Lanka River Basins (${stations.size} Gauges)",
                    style = MaterialTheme.typography.headlineMedium,
                    color = AdaptivePrimaryText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloodSummaryPill(
                        count = majorCount,
                        label = "Major Flood",
                        color = AlertRed,
                        modifier = Modifier.weight(1f)
                    )
                    FloodSummaryPill(
                        count = minorCount,
                        label = "Minor Flood",
                        color = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FloodSummaryPill(
                        count = alertCount,
                        label = "Alert Stage",
                        color = AdvisoryYellow,
                        modifier = Modifier.weight(1f)
                    )
                    FloodSummaryPill(
                        count = normalCount,
                        label = "Normal Level",
                        color = SafeEmerald,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Global Historical Graph Time Range Selector Card (6H, 24H, 3D, 7D)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AdaptiveBorderColor, RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = AdaptiveCyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "HISTORICAL GRAPH TIME RANGE",
                                style = MaterialTheme.typography.labelLarge,
                                color = AdaptiveCyanAccent
                            )
                        }
                        Text(
                            text = selectedHydrographTimeRange.fullTitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = AdaptiveTealAccent
                        )
                    }

                    HydrographTimeRangeSelector(
                        selectedRange = selectedHydrographTimeRange,
                        onRangeSelected = onHydrographTimeRangeSelected,
                        stationIdTagPrefix = "global"
                    )
                }
            }
        }

        // 3. Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text(stringResource(R.string.search_rivers_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AdaptiveCyanAccent
                    )
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_rivers_input")
            )
        }

        // 4. RiverWatch.LK River Basin Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(basinOptions, key = { it }) { basin ->
                    val isSelected = basin == selectedBasinFilter
                    FilterChip(
                        selected = isSelected,
                        onClick = { onBasinFilterSelected(basin) },
                        label = { Text(basin) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RadarCyan.copy(alpha = 0.22f),
                            selectedLabelColor = AdaptiveCyanAccent
                        ),
                        modifier = Modifier.testTag("basin_chip_${basin.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }

        // 5. Station Cards
        items(filteredStations, key = { it.id }) { station ->
            RiverGaugeStationCard(
                station = station,
                globalTimeRange = selectedHydrographTimeRange,
                onGlobalTimeRangeSelected = onHydrographTimeRangeSelected
            )
        }
    }
}

@Composable
private fun FloodSummaryPill(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.14f))
            .border(1.dp, color.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = AdaptivePrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.headlineMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RiverGaugeStationCard(
    station: RiverGaugeStation,
    globalTimeRange: HydrographTimeRange,
    onGlobalTimeRangeSelected: (HydrographTimeRange) -> Unit
) {
    var expanded by remember {
        mutableStateOf(station.floodStatus == FloodStatus.MAJOR_FLOOD || station.id == "kelani_nagalagam")
    }
    var localRangeOverride by remember(globalTimeRange) { mutableStateOf(globalTimeRange) }
    val statusColor = station.floodStatus.color
    val deltaSign = if (station.deltaM >= 0) "+" else ""

    val rainScale = (station.catchmentRainMm24h / 250.0).toFloat().coerceIn(0.06f, 1.0f)
    val rainAccent = when {
        station.catchmentRainMm24h >= 150.0 -> AlertRed
        station.catchmentRainMm24h >= 100.0 -> WarningAmber
        else -> AdaptiveCyanAccent
    }
    val rainSeverityLabel = when {
        station.catchmentRainMm24h >= 150.0 -> "Extreme (>150mm)"
        station.catchmentRainMm24h >= 100.0 -> "Heavy (>100mm)"
        station.catchmentRainMm24h >= 50.0 -> "Moderate (50–100mm)"
        else -> "Light (<50mm)"
    }

    val flowScale = (station.dischargeM3s / 2000.0).toFloat().coerceIn(0.06f, 1.0f)
    val flowAccent = when {
        station.dischargeM3s >= 1200.0 -> AlertRed
        station.dischargeM3s >= 700.0 -> WarningAmber
        else -> AdaptiveTealAccent
    }
    val flowSeverityLabel = when {
        station.dischargeM3s >= 1200.0 -> "Peak Flood Surge"
        station.dischargeM3s >= 700.0 -> "High Basin Flow"
        else -> "Normal Discharge"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (station.floodStatus.severityRank >= 3) 1.5.dp else 1.dp,
                color = if (station.floodStatus.severityRank >= 3) statusColor.copy(alpha = 0.7f) else AdaptiveBorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { expanded = !expanded }
            .testTag("river_station_card_${station.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Water,
                            contentDescription = null,
                            tint = AdaptiveCyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = buildString {
                                append("${station.riverName.uppercase()} • ${station.basinCode}")
                                if (station.tributaryOf != null) {
                                    append(" (${station.tributaryOf})")
                                }
                            },
                            style = MaterialTheme.typography.labelMedium,
                            color = AdaptiveCyanAccent,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = station.stationName,
                        style = MaterialTheme.typography.titleLarge,
                        color = AdaptivePrimaryText
                    )
                    Text(
                        text = "${station.district} District • ${station.lastUpdatedText}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AdaptiveSecondaryText
                    )
                }

                Surface(
                    color = statusColor.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Text(
                            text = station.floodStatus.shortLabel,
                            style = MaterialTheme.typography.labelLarge,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Dedicated Water Level & Hourly Trend Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(AdaptiveElevatedColor)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CURRENT WATER LEVEL",
                        style = MaterialTheme.typography.labelSmall,
                        color = AdaptiveSecondaryText
                    )
                    Text(
                        text = String.format(Locale.US, "%.2f m", station.currentLevelM),
                        style = MaterialTheme.typography.headlineLarge,
                        color = AdaptivePrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = (if (station.deltaM > 0.03) WarningAmber else SafeEmerald).copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${station.trend.symbol} $deltaSign${String.format(Locale.US, "%.2f m/hr", station.deltaM)}",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (station.deltaM > 0.03) WarningAmber else SafeEmerald,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = station.trend.label,
                            style = MaterialTheme.typography.bodySmall,
                            color = AdaptiveSecondaryText
                        )
                    }
                }
            }

            // Scaled 2-Column Catchment Rain (0-250mm) & River Flow (0-2000 m³/s) Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ScaledRiverMetricBox(
                    icon = Icons.Default.WaterDrop,
                    title = "CATCHMENT RAIN (24H)",
                    valueText = "${station.catchmentRainMm24h.roundToInt()} mm",
                    scaleFraction = rainScale,
                    scaleRangeText = "Scale: 0–250 mm",
                    statusText = rainSeverityLabel,
                    accentColor = rainAccent,
                    modifier = Modifier.weight(1f)
                )

                ScaledRiverMetricBox(
                    icon = Icons.Default.Speed,
                    title = "RIVER FLOW RATE",
                    valueText = "${station.dischargeM3s.roundToInt()} m³/s",
                    scaleFraction = flowScale,
                    scaleRangeText = "Scale: 0–2000 m³/s",
                    statusText = flowSeverityLabel,
                    accentColor = flowAccent,
                    modifier = Modifier.weight(1f)
                )
            }

            // Water Level Threshold Bar (Alert / Minor / Major)
            RiverThresholdBar(station = station)

            // Expandable Historical Hydrograph & Downstream At-Risk Towns
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Downstream Watch: ${station.downstreamTowns.take(3).joinToString(", ")}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AdaptiveSecondaryText,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Hide hydrograph" else "Show hydrograph",
                    tint = AdaptiveCyanAccent
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AdaptiveElevatedColor)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "HISTORICAL HYDROGRAPH (${localRangeOverride.fullTitle.uppercase()})",
                            style = MaterialTheme.typography.labelLarge,
                            color = AdaptiveCyanAccent
                        )
                    }

                    HydrographTimeRangeSelector(
                        selectedRange = localRangeOverride,
                        onRangeSelected = { range ->
                            localRangeOverride = range
                            onGlobalTimeRangeSelected(range)
                        },
                        stationIdTagPrefix = station.id
                    )

                    RiverHydrographCurve(
                        station = station,
                        timeRange = localRangeOverride
                    )

                    Text(
                        text = "At-Risk Low-Lying Floodplain Divisions: ${station.downstreamTowns.joinToString(" • ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AdaptivePrimaryText
                    )
                }
            }
        }
    }
}

@Composable
private fun ScaledRiverMetricBox(
    icon: ImageVector,
    title: String,
    valueText: String,
    scaleFraction: Float,
    scaleRangeText: String,
    statusText: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AdaptiveElevatedColor)
            .border(1.dp, accentColor.copy(alpha = 0.40f), RoundedCornerShape(14.dp))
            .padding(11.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = AdaptiveSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = valueText,
            style = MaterialTheme.typography.titleLarge,
            color = AdaptivePrimaryText,
            fontWeight = FontWeight.Bold
        )

        LinearProgressIndicator(
            progress = { scaleFraction },
            color = accentColor,
            trackColor = AdaptiveBorderColor.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )

        Text(
            text = statusText,
            style = MaterialTheme.typography.labelSmall,
            color = accentColor,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = scaleRangeText,
            style = MaterialTheme.typography.bodySmall,
            color = AdaptiveSecondaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
