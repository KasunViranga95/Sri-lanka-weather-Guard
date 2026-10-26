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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Flood
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.model.AlertSeverity
import com.example.model.DispatchedNotificationRecord
import com.example.model.FloodStatus
import com.example.model.GovernmentAnnouncement
import com.example.model.NotificationIntervalOption
import com.example.model.RegionalWeatherReport
import com.example.model.RiverGaugeStation
import com.example.model.SriLankaRegion
import com.example.model.WeatherAlert
import com.example.ui.theme.AdaptiveBorderColor
import com.example.ui.theme.AdaptiveCyanAccent
import com.example.ui.theme.AdaptiveElevatedColor
import com.example.ui.theme.AdaptivePrimaryText
import com.example.ui.theme.AdaptiveSecondaryText
import com.example.ui.theme.AdaptiveSurfaceColor
import com.example.ui.theme.AdaptiveTealAccent
import com.example.ui.theme.AlertRed
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.MonsoonTeal
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.WarningAmber
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun AlertsDashboardScreen(
    regions: List<SriLankaRegion>,
    selectedRegion: SriLankaRegion,
    selectedNearestCity: String,
    selectedNotificationInterval: NotificationIntervalOption,
    gpsStatusMessage: String,
    latestNotification: DispatchedNotificationRecord?,
    residingGovAnnouncement: GovernmentAnnouncement?,
    alerts: List<WeatherAlert>,
    selectedSeverityFilter: AlertSeverity?,
    regionalReport: RegionalWeatherReport,
    riverStations: List<RiverGaugeStation>,
    onRegionSelected: (SriLankaRegion) -> Unit,
    onSelectNotificationInterval: (NotificationIntervalOption) -> Unit,
    onRequestGpsLocation: () -> Unit,
    onOpenResidingLocationPicker: () -> Unit,
    onSendAreaNotificationNow: () -> Unit,
    onRefreshTelemetry: () -> Unit,
    onSeverityFilterSelected: (AlertSeverity?) -> Unit,
    onOpenRadarTab: () -> Unit,
    onOpenRiversTab: () -> Unit,
    onOpenSafetyTab: () -> Unit,
    onOpenOfficialUrl: (String) -> Unit,
    onDialEmergencyDmc: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredAlerts = remember(alerts, selectedSeverityFilter) {
        if (selectedSeverityFilter == null) {
            alerts
        } else {
            alerts.filter { it.severity == selectedSeverityFilter }
        }
    }

    val majorFloodCount = remember(riverStations) {
        riverStations.count { it.floodStatus == FloodStatus.MAJOR_FLOOD }
    }
    val minorFloodCount = remember(riverStations) {
        riverStations.count { it.floodStatus == FloodStatus.MINOR_FLOOD }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("alerts_dashboard_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Hero Atmospheric & Residing Location Command Card
        item {
            HeroCommandBanner(
                selectedRegion = selectedRegion,
                selectedNearestCity = selectedNearestCity,
                gpsStatusMessage = gpsStatusMessage,
                regionalReport = regionalReport,
                majorFloodCount = majorFloodCount,
                minorFloodCount = minorFloodCount,
                riverStationTotal = riverStations.size,
                onRequestGpsLocation = onRequestGpsLocation,
                onOpenResidingLocationPicker = onOpenResidingLocationPicker,
                onSendAreaNotificationNow = onSendAreaNotificationNow,
                onRefreshTelemetry = onRefreshTelemetry,
                onOpenRadarTab = onOpenRadarTab,
                onOpenRiversTab = onOpenRiversTab,
                onDialEmergencyDmc = onDialEmergencyDmc
            )
        }

        // 2. Notification Creation Time Interval Selector (5 min, 15 min, 30 min, 1 hour, 2 hour, 4 hour)
        item {
            NotificationIntervalSelectorCard(
                selectedNearestCity = selectedNearestCity,
                selectedRegionName = selectedRegion.name,
                selectedInterval = selectedNotificationInterval,
                onSelectInterval = onSelectNotificationInterval
            )
        }

        // 3. Dispatched Residing Area Notification Banner (Shows Temp, Precip, Current, Forecast & Gov Official Announcement)
        if (latestNotification != null) {
            item {
                DispatchedNotificationBanner(
                    record = latestNotification,
                    onChangeLocation = onOpenResidingLocationPicker,
                    onSendNow = onSendAreaNotificationNow,
                    onOpenOfficialUrl = { onOpenOfficialUrl(latestNotification.officialPortalUrl) }
                )
            }
        }

        // 4. Official Government Announcement for User's Residing Area with Official News Link
        if (residingGovAnnouncement != null) {
            item {
                ResidingGovernmentBulletinSummaryCard(
                    announcement = residingGovAnnouncement,
                    onOpenOfficialUrl = { onOpenOfficialUrl(residingGovAnnouncement.officialUrl) },
                    onViewAllGovNews = onOpenSafetyTab
                )
            }
        }

        // 5. Sri Lanka 25-District & Nearest City Selector Strip
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SRI LANKA 25 DISTRICTS (${selectedNearestCity.uppercase()})",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveCyanAccent
                    )
                    Text(
                        text = "Search District & City →",
                        style = MaterialTheme.typography.labelMedium,
                        color = AdaptiveTealAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onOpenResidingLocationPicker() }
                            .testTag("search_district_city_link")
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("region_selector_row")
                ) {
                    items(regions, key = { it.id }) { region ->
                        val isSelected = region.id == selectedRegion.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onRegionSelected(region) },
                            label = {
                                Text(
                                    text = region.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            leadingIcon = if (isSelected) {
                                {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RadarCyan.copy(alpha = 0.22f),
                                selectedLabelColor = AdaptiveCyanAccent,
                                selectedLeadingIconColor = AdaptiveCyanAccent
                            ),
                            modifier = Modifier.testTag("region_chip_${region.id}")
                        )
                    }
                }
            }
        }

        // 6. Severity Filter Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REAL-TIME SAFETY ALERTS (${filteredAlerts.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptivePrimaryText
                    )
                    Text(
                        text = regionalReport.syncedTimestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = AdaptiveTealAccent
                    )
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = selectedSeverityFilter == null,
                            onClick = { onSeverityFilterSelected(null) },
                            label = { Text("All Active") },
                            modifier = Modifier.testTag("filter_alert_all")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSeverityFilter == AlertSeverity.CRITICAL_RED,
                            onClick = { onSeverityFilterSelected(AlertSeverity.CRITICAL_RED) },
                            label = { Text("Red Evacuation") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AlertRed.copy(alpha = 0.25f),
                                selectedLabelColor = AlertRed
                            ),
                            modifier = Modifier.testTag("filter_alert_red")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSeverityFilter == AlertSeverity.MAJOR_AMBER,
                            onClick = { onSeverityFilterSelected(AlertSeverity.MAJOR_AMBER) },
                            label = { Text("Amber Flood Watch") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarningAmber.copy(alpha = 0.25f),
                                selectedLabelColor = WarningAmber
                            ),
                            modifier = Modifier.testTag("filter_alert_amber")
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedSeverityFilter == AlertSeverity.ADVISORY_YELLOW,
                            onClick = { onSeverityFilterSelected(AlertSeverity.ADVISORY_YELLOW) },
                            label = { Text("Local / Naval") },
                            modifier = Modifier.testTag("filter_alert_yellow")
                        )
                    }
                }
            }
        }

        // 7. Alert Cards
        items(filteredAlerts, key = { it.id }) { alert ->
            WeatherAlertCard(
                alert = alert,
                onOpenRiversTab = onOpenRiversTab,
                onOpenRadarTab = onOpenRadarTab
            )
        }
    }
}

@Composable
fun NotificationIntervalSelectorCard(
    selectedNearestCity: String,
    selectedRegionName: String,
    selectedInterval: NotificationIntervalOption,
    onSelectInterval: (NotificationIntervalOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, AdaptiveBorderColor, RoundedCornerShape(18.dp))
            .testTag("notification_interval_selector_card")
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
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = AdaptiveTealAccent,
                        modifier = Modifier.size(17.dp)
                    )
                    Text(
                        text = "MOBILE NOTIFICATION HUB INTERVAL",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveTealAccent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Surface(
                    color = AdaptiveTealAccent.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = selectedInterval.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = AdaptiveTealAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = "Auto-dispatches to your phone's notification hub for $selectedNearestCity ($selectedRegionName) including Live Temp, Precipitation, Current Weather, Forecast & Official Gov Announcement:",
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveSecondaryText
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NotificationIntervalOption.entries.toList(), key = { it.id }) { option ->
                    val isSelected = option == selectedInterval
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSelectInterval(option) },
                        label = {
                            Text(
                                text = option.shortChipLabel,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RadarCyan.copy(alpha = 0.22f),
                            selectedLabelColor = AdaptiveCyanAccent
                        ),
                        modifier = Modifier.testTag("interval_chip_${option.id}")
                    )
                }
            }
        }
    }
}

@Composable
private fun ResidingGovernmentBulletinSummaryCard(
    announcement: GovernmentAnnouncement,
    onOpenOfficialUrl: () -> Unit,
    onViewAllGovNews: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AdaptiveElevatedColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.2.dp, RadarCyan.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
            .testTag("residing_gov_announcement_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
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
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = AdaptiveCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "OFFICIAL GOV ANNOUNCEMENT • YOUR AREA",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveCyanAccent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = announcement.referenceCode,
                    style = MaterialTheme.typography.labelMedium,
                    color = AdaptiveTealAccent
                )
            }

            Text(
                text = announcement.headline,
                style = MaterialTheme.typography.titleMedium,
                color = AdaptivePrimaryText,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = announcement.summary,
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveSecondaryText
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpenOfficialUrl,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RadarCyan,
                        contentColor = Color(0xFF041222)
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("open_official_gov_link_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = announcement.officialPortalName,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onViewAllGovNews,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("view_all_gov_news_button")
                ) {
                    Text(
                        text = "All Gov News →",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveTealAccent
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroCommandBanner(
    selectedRegion: SriLankaRegion,
    selectedNearestCity: String,
    gpsStatusMessage: String,
    regionalReport: RegionalWeatherReport,
    majorFloodCount: Int,
    minorFloodCount: Int,
    riverStationTotal: Int,
    onRequestGpsLocation: () -> Unit,
    onOpenResidingLocationPicker: () -> Unit,
    onSendAreaNotificationNow: () -> Unit,
    onRefreshTelemetry: () -> Unit,
    onOpenRadarTab: () -> Unit,
    onOpenRiversTab: () -> Unit,
    onDialEmergencyDmc: () -> Unit
) {
    val isDark = LocalIsDarkTheme.current
    val heroGradient = if (isDark) {
        listOf(Color(0xFF0E2744), Color(0xFF091B30), Color(0xFF151936))
    } else {
        listOf(Color(0xFFE1F3FC), Color(0xFFD5ECF8), Color(0xFFE7F0FA))
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AdaptiveBorderColor, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(colors = heroGradient))
                .padding(16.dp),
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
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(AlertRed)
                        )
                        Text(
                            text = "RESIDING AREA WATCH • SRI LANKA",
                            style = MaterialTheme.typography.labelMedium,
                            color = AdaptiveCyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$selectedNearestCity, ${selectedRegion.name}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AdaptivePrimaryText,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = gpsStatusMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = AdaptiveSecondaryText
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onRequestGpsLocation,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(RadarCyan.copy(alpha = 0.16f))
                            .testTag("gps_detect_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GpsFixed,
                            contentDescription = "Detect GPS Location & Notify",
                            tint = AdaptiveCyanAccent
                        )
                    }
                    IconButton(
                        onClick = onOpenResidingLocationPicker,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MonsoonTeal.copy(alpha = 0.16f))
                            .testTag("open_location_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditLocationAlt,
                            contentDescription = "Search & Select District and Nearest City",
                            tint = AdaptiveTealAccent
                        )
                    }
                    IconButton(
                        onClick = onRefreshTelemetry,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AdaptivePrimaryText.copy(alpha = 0.08f))
                            .testTag("refresh_telemetry_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Live Telemetry",
                            tint = AdaptivePrimaryText
                        )
                    }
                }
            }

            // Residing Area Notification Action Bar (GPS or Manual District/Nearest City Search)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenResidingLocationPicker,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AdaptiveCyanAccent),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("select_district_city_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.EditLocationAlt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("District & City", style = MaterialTheme.typography.labelLarge)
                }

                Button(
                    onClick = onSendAreaNotificationNow,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MonsoonTeal,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("send_area_notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Notify My Area", fontWeight = FontWeight.Bold)
                }
            }

            // 2x2 Live Weather Telemetry Grid (Live Temp, Live Precipitation, Wind Gust, Humidity/Pressure)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryMetricPill(
                        icon = Icons.Default.Thermostat,
                        label = "LIVE TEMPERATURE",
                        value = String.format(Locale.US, "%.1f°C", regionalReport.currentTempC),
                        subtitle = "Feels ${regionalReport.feelsLikeC.roundToInt()}°C • ${regionalReport.weatherDescription}",
                        accentColor = SafeEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryMetricPill(
                        icon = Icons.Default.WaterDrop,
                        label = "LIVE PRECIPITATION",
                        value = "${String.format(Locale.US, "%.1f", regionalReport.precipitationMm)} mm/h",
                        subtitle = "24h Total: ${regionalReport.rain24hMm.roundToInt()} mm",
                        accentColor = if (regionalReport.rain24hMm >= 100) AlertRed else AdaptiveCyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryMetricPill(
                        icon = Icons.Default.Air,
                        label = "MONSOON WIND GUST",
                        value = "${regionalReport.windGustKmh.roundToInt()} km/h",
                        subtitle = "Sustained: ${regionalReport.windSpeedKmh.roundToInt()} km/h",
                        accentColor = if (regionalReport.windGustKmh >= 55) WarningAmber else AdaptiveTealAccent,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryMetricPill(
                        icon = Icons.Default.WaterDrop,
                        label = "HUMIDITY & PRESSURE",
                        value = "${regionalReport.humidityPercent}% RH",
                        subtitle = "Pressure: ${regionalReport.pressureHpa.roundToInt()} hPa",
                        accentColor = AdaptiveCyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // RiverWatch.LK National River Flood Status Strip
            Surface(
                color = AlertRed.copy(alpha = 0.14f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AlertRed.copy(alpha = 0.45f), RoundedCornerShape(14.dp))
                    .clickable { onOpenRiversTab() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Flood,
                            contentDescription = null,
                            tint = AlertRed
                        )
                        Column {
                            Text(
                                text = "RiverWatch.LK • $riverStationTotal Gauges Across 16 Basins",
                                style = MaterialTheme.typography.titleSmall,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$majorFloodCount Major Flood • $minorFloodCount Minor Flood Stations Active",
                                style = MaterialTheme.typography.bodySmall,
                                color = AlertRed
                            )
                        }
                    }
                    Text(
                        text = "View All →",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveCyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenRadarTab,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RadarCyan,
                        contentColor = Color(0xFF041222)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("hero_open_radar_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Radar,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Live Weather Map", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onDialEmergencyDmc,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                    modifier = Modifier.testTag("hero_call_dmc_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneInTalk,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DMC 117", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun DispatchedNotificationBanner(
    record: DispatchedNotificationRecord,
    onChangeLocation: () -> Unit,
    onSendNow: () -> Unit,
    onOpenOfficialUrl: () -> Unit
) {
    val accent = record.severity.badgeColor
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AdaptiveElevatedColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, accent, RoundedCornerShape(18.dp))
            .testTag("dispatched_notification_banner")
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
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "MOBILE NOTIFICATION HUB (${record.intervalLabel.uppercase()})",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = record.timestampText,
                    style = MaterialTheme.typography.bodySmall,
                    color = AdaptiveSecondaryText
                )
            }

            Text(
                text = record.title,
                style = MaterialTheme.typography.titleMedium,
                color = AdaptivePrimaryText,
                fontWeight = FontWeight.Bold
            )

            // Prominent Live Temperature & Live Precipitation Badges inside Notification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = SafeEmerald.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = SafeEmerald,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "LIVE TEMP",
                                style = MaterialTheme.typography.labelSmall,
                                color = AdaptiveSecondaryText
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f°C", record.temperatureC)} (Feels ${record.feelsLikeC.roundToInt()}°C)",
                                style = MaterialTheme.typography.labelLarge,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Surface(
                    color = RadarCyan.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = AdaptiveCyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Column {
                            Text(
                                text = "LIVE PRECIPITATION",
                                style = MaterialTheme.typography.labelSmall,
                                color = AdaptiveSecondaryText
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f mm/h", record.hourlyPrecipitationMm)} (${record.rain24hMm.roundToInt()}mm)",
                                style = MaterialTheme.typography.labelLarge,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Structured Current Weather, Forecast & Gov Official Announcement inside Notification Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AdaptiveSurfaceColor)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = "⛈ Current: ${record.currentWeatherSummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AdaptivePrimaryText
                )
                Text(
                    text = "📅 Forecast: ${record.forecastSummary}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AdaptiveCyanAccent,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "🏛 Gov Official [${record.govReferenceCode}]: ${record.govAnnouncementHeadline}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AdaptiveTealAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Official Gov News ↗",
                    style = MaterialTheme.typography.labelMedium,
                    color = AdaptiveCyanAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onOpenOfficialUrl() }
                )
                Text(
                    text = "Send to Hub Now",
                    style = MaterialTheme.typography.labelMedium,
                    color = SafeEmerald,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSendNow() }
                )
                Text(
                    text = "Change Interval →",
                    style = MaterialTheme.typography.labelMedium,
                    color = AdaptiveTealAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onChangeLocation() }
                )
            }
        }
    }
}

@Composable
private fun TelemetryMetricPill(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String = "",
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AdaptiveSurfaceColor.copy(alpha = 0.90f))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
            .padding(horizontal = 11.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
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
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AdaptiveSecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = AdaptivePrimaryText,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (subtitle.isNotEmpty()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun WeatherAlertCard(
    alert: WeatherAlert,
    onOpenRiversTab: () -> Unit,
    onOpenRadarTab: () -> Unit
) {
    var expanded by remember { mutableStateOf(alert.isUserDistrictMatch || alert.severity == AlertSeverity.CRITICAL_RED) }
    val badgeColor = alert.severity.badgeColor

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = AdaptiveSurfaceColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (alert.isUserDistrictMatch) 1.6.dp else 1.dp,
                color = if (alert.isUserDistrictMatch) badgeColor else AdaptiveBorderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { expanded = !expanded }
            .testTag("alert_card_${alert.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = badgeColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = alert.severity.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = badgeColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (alert.isUserDistrictMatch) {
                        Surface(
                            color = RadarCyan.copy(alpha = 0.20f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "YOUR ZONE",
                                style = MaterialTheme.typography.labelMedium,
                                color = AdaptiveCyanAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse alert" else "Expand alert",
                    tint = AdaptiveSecondaryText
                )
            }

            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleLarge,
                color = AdaptivePrimaryText
            )

            Text(
                text = "${alert.issuingAgency} • ${alert.issuedTimeText}",
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveCyanAccent
            )

            Text(
                text = alert.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = AdaptiveSecondaryText
            )

            // Rainfall & Wind Gust pill row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "24h Catchment: ${String.format(Locale.US, "%.1f mm", alert.rainfallMm24h)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = AdaptiveTealAccent
                )
                Text(
                    text = "•",
                    color = AdaptiveSecondaryText
                )
                Text(
                    text = "Peak Gusts: ${alert.windGustKmh.roundToInt()} km/h",
                    style = MaterialTheme.typography.labelMedium,
                    color = WarningAmber
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(AdaptiveElevatedColor)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CITIZEN SAFETY DIRECTIVES (${alert.validUntilText})",
                            style = MaterialTheme.typography.labelLarge,
                            color = AdaptivePrimaryText
                        )
                    }

                    alert.recommendedActions.forEach { step ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AdaptiveTealAccent,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(15.dp)
                            )
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodySmall,
                                color = AdaptivePrimaryText
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = if (alert.riverBasin?.contains("Ganga") == true || alert.riverBasin?.contains("Oya") == true) {
                                onOpenRiversTab
                            } else {
                                onOpenRadarTab
                            },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (alert.riverBasin?.contains("Ganga") == true || alert.riverBasin?.contains("Oya") == true) {
                                    "Inspect ${alert.riverBasin.substringBefore(" (")} Gauge"
                                } else {
                                    "Track on Live Map"
                                },
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
