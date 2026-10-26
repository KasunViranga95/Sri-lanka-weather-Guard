package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.DbzValueMeaning
import com.example.model.LiveMapMode
import com.example.model.LiveMapOverlayMetric
import com.example.model.RadarDbzMeaningList
import com.example.model.RadarFrame
import com.example.model.RadarLayerType
import com.example.model.RegionalWeatherReport
import com.example.model.RiverGaugeStation
import com.example.model.SriLankaRegion
import com.example.model.ZoomEarthLayer
import com.example.model.getDbzMeaningForValue
import com.example.ui.components.SriLankaLiveWebMap
import com.example.ui.components.SriLankaRadarMapCanvas
import com.example.ui.components.ZoomEarthLiveWebMap
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveRadarScreen(
    liveMapMode: LiveMapMode,
    selectedZoomEarthLayer: ZoomEarthLayer,
    liveMapOverlayMetric: LiveMapOverlayMetric,
    radarFrames: List<RadarFrame>,
    currentFrameIndex: Int,
    isRadarPlaying: Boolean,
    activeLayers: Set<RadarLayerType>,
    stations: List<RiverGaugeStation>,
    selectedRegion: SriLankaRegion,
    selectedNearestCity: String,
    regionalReport: RegionalWeatherReport,
    selectedStation: RiverGaugeStation?,
    onSelectMapMode: (LiveMapMode) -> Unit,
    onSelectZoomEarthLayer: (ZoomEarthLayer) -> Unit,
    onSelectMapOverlayMetric: (LiveMapOverlayMetric) -> Unit,
    onTogglePlayPause: () -> Unit,
    onFrameIndexChanged: (Int) -> Unit,
    onToggleLayer: (RadarLayerType) -> Unit,
    onStationSelected: (RiverGaugeStation) -> Unit,
    onOpenExternalUrl: (String) -> Unit,
    onOpenGeminiClarifier: (String?) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentFrame = radarFrames.getOrElse(currentFrameIndex) {
        radarFrames.first()
    }
    val peakDbzMeaning = getDbzMeaningForValue(currentFrame.peakDbz)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_radar_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Mode Switcher (Live Weather Map vs Windy.com Live vs Doppler Radar)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = AdaptiveCyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "SRI LANKA LIVE WEATHER & RADAR",
                                style = MaterialTheme.typography.labelLarge,
                                color = AdaptiveCyanAccent
                            )
                        }
                        Text(
                            text = "$selectedNearestCity (${selectedRegion.name})",
                            style = MaterialTheme.typography.headlineMedium,
                            color = AdaptivePrimaryText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Surface(
                        color = RadarCyan.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = String.format(
                                Locale.US,
                                "%.1f°C • %.1fmm/h",
                                regionalReport.currentTempC,
                                regionalReport.precipitationMm
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = AdaptiveCyanAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                // Map Mode Toggle Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(LiveMapMode.entries.toList(), key = { it.name }) { mode ->
                        val selected = mode == liveMapMode
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectMapMode(mode) },
                            label = {
                                Text(
                                    text = mode.label,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (mode) {
                                        LiveMapMode.WINDY_LIVE -> Icons.Default.Public
                                        LiveMapMode.INTERACTIVE_LIVE_MAP -> Icons.Default.Map
                                        LiveMapMode.TACTICAL_DOPPLER_CANVAS -> Icons.Default.Radar
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RadarCyan.copy(alpha = 0.22f),
                                selectedLabelColor = AdaptiveCyanAccent,
                                selectedLeadingIconColor = AdaptiveCyanAccent
                            ),
                            modifier = Modifier.testTag("map_mode_${mode.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // 2. Layer Selector for Current Mode
        item {
            when (liveMapMode) {
                LiveMapMode.WINDY_LIVE -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(ZoomEarthLayer.entries.toList(), key = { it.name }) { layer ->
                            val selected = layer == selectedZoomEarthLayer
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectZoomEarthLayer(layer) },
                                label = { Text(layer.label) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RadarCyan.copy(alpha = 0.22f),
                                    selectedLabelColor = AdaptiveCyanAccent,
                                    selectedLeadingIconColor = AdaptiveCyanAccent
                                ),
                                modifier = Modifier.testTag("zoom_earth_layer_${layer.name.lowercase()}")
                            )
                        }
                    }
                }

                LiveMapMode.INTERACTIVE_LIVE_MAP -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(LiveMapOverlayMetric.entries.toList(), key = { it.name }) { metric ->
                            val selected = metric == liveMapOverlayMetric
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectMapOverlayMetric(metric) },
                                label = { Text(metric.label) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AdaptiveTealAccent.copy(alpha = 0.20f),
                                    selectedLabelColor = AdaptiveTealAccent,
                                    selectedLeadingIconColor = AdaptiveTealAccent
                                ),
                                modifier = Modifier.testTag("live_map_overlay_${metric.code}")
                            )
                        }
                    }
                }

                LiveMapMode.TACTICAL_DOPPLER_CANVAS -> {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(RadarLayerType.entries.toList(), key = { it.name }) { layer ->
                            val isSelected = activeLayers.contains(layer)
                            FilterChip(
                                selected = isSelected,
                                onClick = { onToggleLayer(layer) },
                                label = { Text(layer.label) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Layers,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RadarCyan.copy(alpha = 0.22f),
                                    selectedLabelColor = AdaptiveCyanAccent,
                                    selectedLeadingIconColor = AdaptiveCyanAccent
                                ),
                                modifier = Modifier.testTag("radar_layer_${layer.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }

        // 3. Map Viewport (Full-window height so the live map fills the screen clearly)
        item {
            when (liveMapMode) {
                LiveMapMode.WINDY_LIVE -> {
                    ZoomEarthLiveWebMap(
                        selectedRegion = selectedRegion,
                        nearestCity = selectedNearestCity,
                        selectedLayer = selectedZoomEarthLayer,
                        regionalReport = regionalReport,
                        onOpenInExternalBrowser = onOpenExternalUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                    )
                }

                LiveMapMode.INTERACTIVE_LIVE_MAP -> {
                    SriLankaLiveWebMap(
                        selectedRegion = selectedRegion,
                        nearestCity = selectedNearestCity,
                        stations = stations,
                        regionalReport = regionalReport,
                        overlayMetric = liveMapOverlayMetric,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)
                    )
                }

                LiveMapMode.TACTICAL_DOPPLER_CANVAS -> {
                    SriLankaRadarMapCanvas(
                        currentFrame = currentFrame,
                        activeLayers = activeLayers,
                        stations = stations,
                        selectedRegion = selectedRegion,
                        selectedStation = selectedStation,
                        onStationTapped = onStationSelected,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(480.dp)
                    )
                }
            }
        }

        // 4. Radar Playback Controls & Timeline Scrubber (Meaning shown after the dBZ value)
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        IconButton(
                            onClick = onTogglePlayPause,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(RadarCyan)
                                .testTag("radar_play_pause_button")
                        ) {
                            Icon(
                                imageVector = if (isRadarPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isRadarPlaying) "Pause Radar Loop" else "Play Radar Loop",
                                tint = Color(0xFF041222)
                            )
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text(
                                text = currentFrame.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentFrame.peakDbz} dBZ — ${peakDbzMeaning.shortTitle} (${peakDbzMeaning.rainRateMmPerHour})",
                                style = MaterialTheme.typography.labelLarge,
                                color = peakDbzMeaning.badgeColor,
                                fontWeight = FontWeight.Bold
                            )
                            Slider(
                                value = currentFrameIndex.toFloat(),
                                onValueChange = { onFrameIndexChanged(it.toInt()) },
                                valueRange = 0f..(radarFrames.size - 1).coerceAtLeast(1).toFloat(),
                                steps = (radarFrames.size - 2).coerceAtLeast(0),
                                colors = SliderDefaults.colors(
                                    thumbColor = RadarCyan,
                                    activeTrackColor = RadarCyan
                                ),
                                modifier = Modifier.testTag("radar_timeline_slider")
                            )
                        }
                    }

                    // dBZ Reflectivity Color Legend Strip with Meaning After Each Value
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DbzLegendItem("15–25 dBZ: Light Rain", SafeEmerald)
                        DbzLegendItem("26–35 dBZ: Moderate", RadarCyan)
                        DbzLegendItem("36–45 dBZ: Heavy Downpour", AdvisoryYellow)
                        DbzLegendItem("46–52 dBZ: Squall", WarningAmber)
                        DbzLegendItem("53+ dBZ: Cloudburst", AlertRed)
                    }
                }
            }
        }

        // 5. Easy-to-Understand Radar dBZ Values Meaning & Safety Guide Card
        item {
            RadarDbzMeaningGuideCard(
                currentPeakDbz = currentFrame.peakDbz,
                residingCity = selectedNearestCity,
                onClarifyWithGemini = {
                    onOpenGeminiClarifier("Explain ${currentFrame.peakDbz} dBZ radar reflectivity and what each dBZ range means for $selectedNearestCity")
                }
            )
        }

        // 6. Live Atmospheric & Precipitation Details Strip for Residing Area
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "LIVE PRECIPITATION & ATMOSPHERIC TELEMETRY",
                                style = MaterialTheme.typography.labelLarge,
                                color = AdaptiveCyanAccent
                            )
                            Text(
                                text = "$selectedNearestCity, ${selectedRegion.name}",
                                style = MaterialTheme.typography.labelMedium,
                                color = AdaptiveTealAccent
                            )
                        }

                        Surface(
                            color = RadarCyan.copy(alpha = 0.16f),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .border(1.dp, RadarCyan.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                                .clickable {
                                    onOpenGeminiClarifier(
                                        "Clarify the live weather metrics (${regionalReport.rain24hMm.roundToInt()}mm/24h rain, ${regionalReport.pressureHpa.roundToInt()} hPa pressure, ${regionalReport.windGustKmh.roundToInt()} km/h gusts) in $selectedNearestCity"
                                    )
                                }
                                .testTag("live_radar_ask_gemini_metrics_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AdaptiveCyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Clarify Metrics",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AdaptiveCyanAccent,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MapDetailMetricBox(
                            icon = Icons.Default.WaterDrop,
                            label = "Precipitation (24h)",
                            value = "${String.format(Locale.US, "%.1f", regionalReport.rain24hMm)} mm",
                            sub = "Hourly: ${String.format(Locale.US, "%.1f", regionalReport.precipitationMm)} mm/h",
                            accent = if (regionalReport.rain24hMm >= 100) AlertRed else AdaptiveCyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                        MapDetailMetricBox(
                            icon = Icons.Default.Air,
                            label = "Wind & Gusts",
                            value = "${regionalReport.windGustKmh.roundToInt()} km/h",
                            sub = "Sustained: ${regionalReport.windSpeedKmh.roundToInt()} km/h",
                            accent = WarningAmber,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MapDetailMetricBox(
                            icon = Icons.Default.Thermostat,
                            label = "Temp & Humidity",
                            value = "${regionalReport.currentTempC.roundToInt()}°C • ${regionalReport.humidityPercent}%",
                            sub = "Feels Like ${regionalReport.feelsLikeC.roundToInt()}°C",
                            accent = SafeEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        MapDetailMetricBox(
                            icon = Icons.Default.Speed,
                            label = "Surface Pressure",
                            value = "${regionalReport.pressureHpa.roundToInt()} hPa",
                            sub = regionalReport.weatherDescription,
                            accent = AdaptiveTealAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 7. Tapped River Gauge Pin Inspector
        item {
            val inspected = selectedStation ?: stations.maxByOrNull { it.floodStatus.severityRank }
            if (inspected != null) {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AdaptiveElevatedColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, inspected.floodStatus.color.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                                    imageVector = Icons.Default.Water,
                                    contentDescription = null,
                                    tint = inspected.floodStatus.color,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "RIVERWATCH PIN: ${inspected.stationName.uppercase()}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = AdaptivePrimaryText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Surface(
                                color = inspected.floodStatus.color.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = inspected.floodStatus.shortLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = inspected.floodStatus.color,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                        Text(
                            text = "${inspected.riverName} (${inspected.basinCode}) • Water Level: ${
                                String.format(Locale.US, "%.2f m", inspected.currentLevelM)
                            } (Major Flood: ${String.format(Locale.US, "%.2f m", inspected.majorFloodLevelM)})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AdaptiveSecondaryText
                        )
                        Text(
                            text = "24h Catchment Rain: ${inspected.catchmentRainMm24h.roundToInt()} mm • Flow: ${inspected.dischargeM3s.roundToInt()} m³/s",
                            style = MaterialTheme.typography.bodySmall,
                            color = AdaptiveCyanAccent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 8. Active Convective Storm Cells List (Showing dBZ meaning after each value)
        item {
            Text(
                text = "ACTIVE MONSOON CONVECTIVE CELLS",
                style = MaterialTheme.typography.labelLarge,
                color = AdaptivePrimaryText
            )
        }

        items(currentFrame.activeCells, key = { it.id }) { cell ->
            val cellDbzMeaning = getDbzMeaningForValue(cell.intensityDbz)
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AdaptiveBorderColor, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(cell.color)
                            )
                            Text(
                                text = cell.label,
                                style = MaterialTheme.typography.titleMedium,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tracking ENE (${cell.movementDeg.toInt()}°)",
                            style = MaterialTheme.typography.bodySmall,
                            color = AdaptiveSecondaryText
                        )
                    }

                    // Show dBZ Value first, then its meaning right after the value
                    Text(
                        text = "${cell.intensityDbz} dBZ — ${cellDbzMeaning.shortTitle} (${cellDbzMeaning.rainRateMmPerHour}): ${cellDbzMeaning.weatherMeaning}",
                        style = MaterialTheme.typography.bodySmall,
                        color = cell.color,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun RadarDbzMeaningGuideCard(
    currentPeakDbz: Int,
    residingCity: String,
    onClarifyWithGemini: () -> Unit
) {
    val currentMeaning = getDbzMeaningForValue(currentPeakDbz)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, RadarCyan.copy(alpha = 0.50f), RoundedCornerShape(18.dp))
            .testTag("dbz_meaning_guide_card")
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AdaptiveCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "RADAR dBZ VALUES & MEANING",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveCyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = RadarCyan.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .border(1.dp, RadarCyan.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .clickable { onClarifyWithGemini() }
                        .testTag("dbz_card_ask_gemini_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AdaptiveCyanAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Ask Gemini",
                            style = MaterialTheme.typography.labelSmall,
                            color = AdaptiveCyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Current Peak Value followed by its meaning
            Surface(
                color = currentMeaning.badgeColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, currentMeaning.badgeColor.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
            ) {
                Text(
                    text = "Current Peak: $currentPeakDbz dBZ — ${currentMeaning.shortTitle} (${currentMeaning.rainRateMmPerHour})",
                    style = MaterialTheme.typography.labelLarge,
                    color = currentMeaning.badgeColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                )
            }

            Text(
                text = "dBZ (Decibels of Z) measures radar energy reflected by raindrops over $residingCity & Sri Lanka. Each dBZ value range below is followed by its weather meaning and safety action:",
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveSecondaryText
            )

            RadarDbzMeaningList.forEach { item ->
                val isCurrentPeakBand = currentPeakDbz in item.minDbz..item.maxDbz
                DbzMeaningRowItem(
                    item = item,
                    isHighlighted = isCurrentPeakBand
                )
            }
        }
    }
}

@Composable
private fun DbzMeaningRowItem(
    item: DbzValueMeaning,
    isHighlighted: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isHighlighted) item.badgeColor.copy(alpha = 0.14f) else AdaptiveElevatedColor
            )
            .border(
                width = if (isHighlighted) 1.5.dp else 1.dp,
                color = if (isHighlighted) item.badgeColor else AdaptiveBorderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(11.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // 1. Value & Rain Rate first
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = item.badgeColor.copy(alpha = 0.22f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = item.dbzRange,
                    style = MaterialTheme.typography.labelMedium,
                    color = item.badgeColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Text(
                text = "Rain Rate: ${item.rainRateMmPerHour}",
                style = MaterialTheme.typography.labelMedium,
                color = item.badgeColor,
                fontWeight = FontWeight.Bold
            )
        }

        // 2. Meaning shown clearly AFTER the value (never over it)
        Text(
            text = "Meaning: ${item.shortTitle} — ${item.weatherMeaning}",
            style = MaterialTheme.typography.bodySmall,
            color = AdaptivePrimaryText,
            fontWeight = FontWeight.SemiBold
        )

        // 3. Citizen Safety Action
        Text(
            text = "Safety Action: ${item.citizenAction}",
            style = MaterialTheme.typography.labelSmall,
            color = AdaptiveSecondaryText,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MapDetailMetricBox(
    icon: ImageVector,
    label: String,
    value: String,
    sub: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AdaptiveElevatedColor)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = AdaptiveSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = AdaptivePrimaryText,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = sub,
            style = MaterialTheme.typography.bodySmall,
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DbzLegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = AdaptivePrimaryText,
            fontWeight = FontWeight.Medium
        )
    }
}
