package com.example

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.Flood
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.outlined.CompassCalibration
import androidx.compose.material.icons.outlined.Flood
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AlertSeverity
import com.example.model.AppTab
import com.example.model.FloodStatus
import com.example.ui.components.GeminiClarificationDialog
import com.example.ui.components.ResidingLocationDialog
import com.example.ui.screens.AlertsDashboardScreen
import com.example.ui.screens.LiveRadarScreen
import com.example.ui.screens.RegionalForecastScreen
import com.example.ui.screens.RiverWaterLevelsScreen
import com.example.ui.screens.SafetyHubScreen
import com.example.ui.theme.AdaptiveAppBackground
import com.example.ui.theme.AdaptiveBorderColor
import com.example.ui.theme.AdaptiveCyanAccent
import com.example.ui.theme.AdaptiveElevatedColor
import com.example.ui.theme.AdaptivePrimaryText
import com.example.ui.theme.AdaptiveSecondaryText
import com.example.ui.theme.AdaptiveSurfaceColor
import com.example.ui.theme.AlertRed
import com.example.ui.theme.LankaWeatherGuardTheme
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.WarningAmber
import com.example.util.WeatherNotificationHelper
import com.example.viewmodel.WeatherViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WeatherNotificationHelper.createNotificationChannel(this)
        enableEdgeToEdge()
        setContent {
            LankaWeatherGuardApp()
        }
    }
}

@Composable
fun LankaWeatherGuardApp(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LankaWeatherGuardTheme(darkTheme = uiState.isDarkTheme) {
        // Notification Permission Launcher (Android 13+)
        val notificationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { _ ->
            weatherViewModel.triggerNotificationForCurrentArea(context)
        }

        // Location Permission Launcher: if denied or GPS unavailable, opens the Search & Select District/Nearest City Dialog
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (granted) {
                resolveDeviceLocation(
                    context = context,
                    onCoordinates = { lat, lon ->
                        weatherViewModel.onCoordinatesDetectedAndNotify(context, lat, lon)
                    },
                    onFallback = {
                        weatherViewModel.openResidingLocationDialog(permissionDenied = true)
                    }
                )
            } else {
                weatherViewModel.openResidingLocationDialog(permissionDenied = true)
            }
        }

        // Generate initial residing area notification and start interval schedule on launch
        LaunchedEffect(Unit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            weatherViewModel.triggerNotificationForCurrentArea(context)
        }

        val requestLocationAction = {
            val hasFine = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val hasCoarse = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasFine || hasCoarse) {
                resolveDeviceLocation(
                    context = context,
                    onCoordinates = { lat, lon ->
                        weatherViewModel.onCoordinatesDetectedAndNotify(context, lat, lon)
                    },
                    onFallback = {
                        weatherViewModel.openResidingLocationDialog(permissionDenied = false)
                    }
                )
            } else {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }

        val sendNotificationForCurrentAreaAction = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                weatherViewModel.triggerNotificationForCurrentArea(context)
            }
        }

        // Residing Area Search & Select Dialog (District + Nearest City + Notification Interval)
        if (uiState.showResidingLocationDialog) {
            ResidingLocationDialog(
                regions = uiState.regions,
                initialRegion = uiState.selectedRegion,
                initialCity = uiState.selectedNearestCity,
                initialInterval = uiState.selectedNotificationInterval,
                wasPermissionDenied = uiState.wasLocationPermissionDenied,
                onDismiss = { weatherViewModel.dismissResidingLocationDialog() },
                onConfirmSelection = { chosenRegion, chosenCity, chosenInterval ->
                    weatherViewModel.confirmResidingLocationAndNotify(
                        context = context,
                        region = chosenRegion,
                        nearestCity = chosenCity,
                        fromGps = false,
                        intervalOption = chosenInterval
                    )
                }
            )
        }

        // Gemini Weather & Metric Clarifier Dialog
        if (uiState.showGeminiClarifierDialog) {
            val currentPeakDbz = uiState.radarFrames.getOrElse(uiState.currentRadarFrameIndex) {
                uiState.radarFrames.firstOrNull()
            }?.peakDbz ?: 48
            val primaryRiverStation = uiState.riverStations.firstOrNull {
                it.district.equals(uiState.selectedRegion.name, ignoreCase = true)
            } ?: uiState.riverStations.maxByOrNull { it.floodStatus.severityRank }

            GeminiClarificationDialog(
                selectedRegion = uiState.selectedRegion,
                selectedNearestCity = uiState.selectedNearestCity,
                regionalReport = uiState.regionalReport,
                peakDbz = currentPeakDbz,
                primaryStation = primaryRiverStation,
                messages = uiState.geminiMessages,
                isThinking = uiState.isGeminiThinking,
                onAskQuestion = { question -> weatherViewModel.askGeminiClarification(question) },
                onDismiss = { weatherViewModel.dismissGeminiClarifier() }
            )
        }

        // BackHandler on secondary tabs returns to the primary Alerts screen
        if (uiState.currentTab != AppTab.ALERTS) {
            BackHandler {
                weatherViewModel.selectTab(AppTab.ALERTS)
            }
        }

        val criticalAlertCount = uiState.alerts.count { it.severity == AlertSeverity.CRITICAL_RED }
        val floodStationCount = uiState.riverStations.count {
            it.floodStatus == FloodStatus.MAJOR_FLOOD || it.floodStatus == FloodStatus.MINOR_FLOOD
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = AdaptiveAppBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AdaptiveSurfaceColor)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.header_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(R.string.header_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = AdaptiveCyanAccent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Light / Dark Mode Theme Toggle Button
                            Surface(
                                color = AdaptiveElevatedColor,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .border(1.dp, AdaptiveBorderColor, RoundedCornerShape(12.dp))
                                    .clickable { weatherViewModel.toggleDarkTheme() }
                                    .testTag("theme_mode_toggle_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (uiState.isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = if (uiState.isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                                        tint = if (uiState.isDarkTheme) WarningAmber else AdaptiveCyanAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = if (uiState.isDarkTheme) "Light" else "Dark",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AdaptivePrimaryText,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Active Residing City & Interval Chip in Top Bar
                            Surface(
                                color = RadarCyan.copy(alpha = 0.16f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .clickable {
                                        weatherViewModel.openResidingLocationDialog(permissionDenied = false)
                                    }
                                    .testTag("top_bar_residing_area_chip")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .background(SafeEmerald)
                                    )
                                    Text(
                                        text = "${uiState.selectedNearestCity} • ${uiState.selectedNotificationInterval.shortChipLabel}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AdaptivePrimaryText,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Icon(
                                        imageVector = Icons.Default.EditLocationAlt,
                                        contentDescription = "Change Residing Area & Interval",
                                        tint = AdaptiveCyanAccent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Persistent Live Temperature, Precipitation & Gemini Metric Clarifier Bar across all tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AdaptiveElevatedColor)
                            .border(1.dp, AdaptiveBorderColor, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("top_bar_live_temp_precip_strip"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🌡 ${
                                String.format(Locale.US, "%.1f°C", uiState.regionalReport.currentTempC)
                            } • 🌧 ${
                                String.format(Locale.US, "%.1f mm/h", uiState.regionalReport.precipitationMm)
                            } (${uiState.regionalReport.rain24hMm.toInt()}mm/24h)",
                            style = MaterialTheme.typography.labelSmall,
                            color = SafeEmerald,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Surface(
                            color = RadarCyan.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .border(1.dp, RadarCyan.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
                                .clickable { weatherViewModel.openGeminiClarifier() }
                                .testTag("top_bar_ask_gemini_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Ask Gemini to Clarify Metrics",
                                    tint = AdaptiveCyanAccent,
                                    modifier = Modifier.size(13.dp)
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

                    if (uiState.isLoading) {
                        LinearProgressIndicator(
                            color = RadarCyan,
                            trackColor = AdaptiveElevatedColor,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = AdaptiveSurfaceColor,
                    tonalElevation = 8.dp
                ) {
                    AppTab.entries.forEach { tab ->
                        val selected = uiState.currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = { weatherViewModel.selectTab(tab) },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (tab == AppTab.ALERTS && criticalAlertCount > 0) {
                                            Badge(containerColor = AlertRed) {
                                                Text(criticalAlertCount.toString(), color = Color.White)
                                            }
                                        } else if (tab == AppTab.RIVERS && floodStationCount > 0) {
                                            Badge(containerColor = AlertRed) {
                                                Text(floodStationCount.toString(), color = Color.White)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = when (tab) {
                                            AppTab.ALERTS -> if (selected) Icons.Filled.NotificationsActive else Icons.Outlined.NotificationsActive
                                            AppTab.RADAR -> if (selected) Icons.Filled.Radar else Icons.Outlined.Radar
                                            AppTab.RIVERS -> if (selected) Icons.Filled.Flood else Icons.Outlined.Flood
                                            AppTab.FORECAST -> if (selected) Icons.Filled.CompassCalibration else Icons.Outlined.CompassCalibration
                                            AppTab.SAFETY -> if (selected) Icons.Filled.HealthAndSafety else Icons.Outlined.HealthAndSafety
                                        },
                                        contentDescription = tab.label
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AdaptiveCyanAccent,
                                selectedTextColor = AdaptiveCyanAccent,
                                indicatorColor = RadarCyan.copy(alpha = 0.16f),
                                unselectedIconColor = AdaptiveSecondaryText,
                                unselectedTextColor = AdaptiveSecondaryText
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.route}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.currentTab) {
                    AppTab.ALERTS -> AlertsDashboardScreen(
                        regions = uiState.regions,
                        selectedRegion = uiState.selectedRegion,
                        selectedNearestCity = uiState.selectedNearestCity,
                        selectedNotificationInterval = uiState.selectedNotificationInterval,
                        gpsStatusMessage = uiState.gpsStatusMessage,
                        latestNotification = uiState.latestNotification,
                        residingGovAnnouncement = uiState.governmentAnnouncements.firstOrNull(),
                        alerts = uiState.alerts,
                        selectedSeverityFilter = uiState.selectedAlertSeverityFilter,
                        regionalReport = uiState.regionalReport,
                        riverStations = uiState.riverStations,
                        onRegionSelected = { region ->
                            weatherViewModel.confirmResidingLocationAndNotify(
                                context = context,
                                region = region,
                                nearestCity = region.nearestCities.firstOrNull() ?: region.name,
                                fromGps = false
                            )
                        },
                        onSelectNotificationInterval = { interval ->
                            weatherViewModel.selectNotificationInterval(context, interval)
                        },
                        onRequestGpsLocation = requestLocationAction,
                        onOpenResidingLocationPicker = {
                            weatherViewModel.openResidingLocationDialog(permissionDenied = false)
                        },
                        onSendAreaNotificationNow = sendNotificationForCurrentAreaAction,
                        onRefreshTelemetry = { weatherViewModel.refreshAllTelemetry() },
                        onSeverityFilterSelected = { weatherViewModel.setAlertSeverityFilter(it) },
                        onOpenRadarTab = { weatherViewModel.selectTab(AppTab.RADAR) },
                        onOpenRiversTab = { weatherViewModel.selectTab(AppTab.RIVERS) },
                        onOpenSafetyTab = { weatherViewModel.selectTab(AppTab.SAFETY) },
                        onOpenOfficialUrl = { url -> openExternalGovernmentUrl(context, url) },
                        onDialEmergencyDmc = { dialEmergencyNumber(context, "117") }
                    )

                    AppTab.RADAR -> LiveRadarScreen(
                        liveMapMode = uiState.liveMapMode,
                        selectedZoomEarthLayer = uiState.selectedZoomEarthLayer,
                        liveMapOverlayMetric = uiState.liveMapOverlayMetric,
                        radarFrames = uiState.radarFrames,
                        currentFrameIndex = uiState.currentRadarFrameIndex,
                        isRadarPlaying = uiState.isRadarPlaying,
                        activeLayers = uiState.activeRadarLayers,
                        stations = uiState.riverStations,
                        selectedRegion = uiState.selectedRegion,
                        selectedNearestCity = uiState.selectedNearestCity,
                        regionalReport = uiState.regionalReport,
                        selectedStation = uiState.selectedStationDetail,
                        onSelectMapMode = { weatherViewModel.setLiveMapMode(it) },
                        onSelectZoomEarthLayer = { weatherViewModel.setZoomEarthLayer(it) },
                        onSelectMapOverlayMetric = { weatherViewModel.setLiveMapOverlayMetric(it) },
                        onTogglePlayPause = { weatherViewModel.toggleRadarPlayPause() },
                        onFrameIndexChanged = { weatherViewModel.setRadarFrameIndex(it) },
                        onToggleLayer = { weatherViewModel.toggleRadarLayer(it) },
                        onStationSelected = { weatherViewModel.selectStationDetail(it) },
                        onOpenExternalUrl = { url -> openExternalGovernmentUrl(context, url) },
                        onOpenGeminiClarifier = { prompt -> weatherViewModel.openGeminiClarifier(prompt) }
                    )

                    AppTab.RIVERS -> RiverWaterLevelsScreen(
                        stations = uiState.riverStations,
                        selectedBasinFilter = uiState.selectedBasinFilter,
                        searchQuery = uiState.riverSearchQuery,
                        selectedHydrographTimeRange = uiState.selectedHydrographTimeRange,
                        onBasinFilterSelected = { weatherViewModel.setBasinFilter(it) },
                        onSearchQueryChanged = { weatherViewModel.setRiverSearchQuery(it) },
                        onHydrographTimeRangeSelected = { weatherViewModel.setHydrographTimeRange(it) }
                    )

                    AppTab.FORECAST -> RegionalForecastScreen(
                        regions = uiState.regions,
                        selectedRegion = uiState.selectedRegion,
                        report = uiState.regionalReport,
                        onRegionSelected = { region ->
                            weatherViewModel.selectRegion(region)
                        }
                    )

                    AppTab.SAFETY -> SafetyHubScreen(
                        selectedRegion = uiState.selectedRegion,
                        selectedNearestCity = uiState.selectedNearestCity,
                        selectedNotificationInterval = uiState.selectedNotificationInterval,
                        governmentAnnouncements = uiState.governmentAnnouncements,
                        selectedGovCategoryFilter = uiState.selectedGovCategoryFilter,
                        shelters = uiState.shelters,
                        checklist = uiState.checklist,
                        onSelectGovCategoryFilter = { weatherViewModel.setGovCategoryFilter(it) },
                        onSelectNotificationInterval = { interval ->
                            weatherViewModel.selectNotificationInterval(context, interval)
                        },
                        onOpenOfficialUrl = { url -> openExternalGovernmentUrl(context, url) },
                        onToggleChecklistItem = { weatherViewModel.toggleChecklistItem(it) },
                        onDialNumber = { dialEmergencyNumber(context, it) },
                        onShareSafetyReport = {
                            val topRiver = uiState.riverStations.maxByOrNull { it.floodStatus.severityRank }
                            val shareText = buildString {
                                appendLine("🇱🇰 Lanka Weather Guard — Official Situation Report")
                                appendLine("Residing Area: ${uiState.selectedNearestCity}, ${uiState.selectedRegion.name} (${uiState.selectedRegion.primaryRiverBasin})")
                                appendLine("Auto-Notification Interval: ${uiState.selectedNotificationInterval.label}")
                                appendLine("Weather: ${uiState.regionalReport.weatherDescription}, ${String.format(Locale.US, "%.1f", uiState.regionalReport.rain24hMm)}mm/24h, Gusts ${uiState.regionalReport.windGustKmh.toInt()} km/h")
                                if (topRiver != null) {
                                    appendLine("Critical River Gauge: ${topRiver.riverName} at ${topRiver.stationName} = ${String.format(Locale.US, "%.2fm", topRiver.currentLevelM)} (${topRiver.floodStatus.label})")
                                }
                                appendLine("Official Portals: https://www.dmc.gov.lk | https://riverwatch.lk | https://www.news.lk")
                                appendLine("Emergency DMC Hotline: 117 | Suwa Seriya: 1990")
                            }
                            shareSituationText(context, shareText)
                        }
                    )
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun resolveDeviceLocation(
    context: Context,
    onCoordinates: (Double, Double) -> Unit,
    onFallback: () -> Unit
) {
    try {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val gpsLoc = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val netLoc = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val best = gpsLoc ?: netLoc
        if (best != null) {
            onCoordinates(best.latitude, best.longitude)
        } else {
            onFallback()
        }
    } catch (_: Exception) {
        onFallback()
    }
}

private fun openExternalGovernmentUrl(context: Context, url: String) {
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(browserIntent)
    } catch (_: Exception) {
        // Ignore if no browser activity on headless test device
    }
}

private fun dialEmergencyNumber(context: Context, phone: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        // Ignore if no dialer activity on headless test device
    }
}

private fun shareSituationText(context: Context, message: String) {
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }
        val chooser = Intent.createChooser(sendIntent, "Share Weather Situation Report").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    } catch (_: Exception) {
        // Ignore if no chooser activity on headless test device
    }
}
