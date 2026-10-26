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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.EmergencyShelter
import com.example.model.GovernmentAnnouncement
import com.example.model.NotificationIntervalOption
import com.example.model.SafetyChecklistItem
import com.example.model.SriLankaRegion
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

data class EmergencyHotline(
    val agency: String,
    val number: String,
    val role: String,
    val color: Color
)

@Composable
fun SafetyHubScreen(
    selectedRegion: SriLankaRegion,
    selectedNearestCity: String,
    selectedNotificationInterval: NotificationIntervalOption,
    governmentAnnouncements: List<GovernmentAnnouncement>,
    selectedGovCategoryFilter: String,
    shelters: List<EmergencyShelter>,
    checklist: List<SafetyChecklistItem>,
    onSelectGovCategoryFilter: (String) -> Unit,
    onSelectNotificationInterval: (NotificationIntervalOption) -> Unit,
    onOpenOfficialUrl: (String) -> Unit,
    onToggleChecklistItem: (String) -> Unit,
    onDialNumber: (String) -> Unit,
    onShareSafetyReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hotlines = listOf(
        EmergencyHotline("DMC Emergency Ops", "117", "24/7 Disaster Relief & Evacuation", AlertRed),
        EmergencyHotline("Suwa Seriya Ambulance", "1990", "Free National Pre-Hospital Ambulance", SafeEmerald),
        EmergencyHotline("Police Emergency", "119", "Immediate Rescue & Traffic Dispatch", WarningAmber),
        EmergencyHotline("SL Navy Flood Rescue", "0112445368", "Rapid Boat Deployment (Kalutara/Ratnapura/Galle)", RadarCyan),
        EmergencyHotline("NBRO Landslide Desk", "0112588946", "Slope Failure & Early Warning Hotline", MonsoonTeal),
        EmergencyHotline("Dept of Meteorology", "0112686686", "National Cyclone & Monsoon Desk", RadarCyan)
    )

    val govCategories = listOf(
        "All Official",
        "My Residing Area",
        "DMC Flood Bulletin",
        "Met Dept Warning",
        "NBRO Landslide",
        "Public Services & Schools"
    )

    val residingAnnouncement = governmentAnnouncements.firstOrNull { it.category == "Residing Area Directive" }
        ?: governmentAnnouncements.firstOrNull()

    val filteredGovNews = remember(governmentAnnouncements, selectedGovCategoryFilter) {
        when (selectedGovCategoryFilter) {
            "All Official" -> governmentAnnouncements
            "My Residing Area" -> governmentAnnouncements.filter { it.isResidingAreaMatch }
            else -> governmentAnnouncements.filter {
                it.category.equals(selectedGovCategoryFilter, ignoreCase = true)
            }
        }
    }

    val completedCount = checklist.count { it.isChecked }
    val progress = if (checklist.isNotEmpty()) completedCount.toFloat() / checklist.size else 0f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("safety_hub_list"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header & Situation Share Button
        item {
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
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = SafeEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "GOVERNMENT ANNOUNCEMENTS & SAFETY HUB",
                            style = MaterialTheme.typography.labelLarge,
                            color = SafeEmerald
                        )
                    }
                    Text(
                        text = "$selectedNearestCity (${selectedRegion.name})",
                        style = MaterialTheme.typography.headlineMedium,
                        color = AdaptivePrimaryText
                    )
                }

                Button(
                    onClick = onShareSafetyReport,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RadarCyan,
                        contentColor = Color(0xFF041222)
                    ),
                    modifier = Modifier.testTag("share_safety_report_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Status", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 2. Featured Official Government Announcement for User's Residing Area with Official Portal Link
        if (residingAnnouncement != null) {
            item {
                ResidingAreaOfficialGovernmentCard(
                    announcement = residingAnnouncement,
                    selectedNearestCity = selectedNearestCity,
                    selectedDistrict = selectedRegion.name,
                    onOpenOfficialUrl = { onOpenOfficialUrl(residingAnnouncement.officialUrl) }
                )
            }
        }

        // 3. Latest Sri Lanka Government Weather Announcements & Official News Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = null,
                        tint = AdaptiveCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "LATEST SRI LANKA GOVERNMENT ANNOUNCEMENTS & WEATHER NEWS",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptivePrimaryText
                    )
                }

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(govCategories, key = { it }) { cat ->
                        val selected = cat == selectedGovCategoryFilter
                        FilterChip(
                            selected = selected,
                            onClick = { onSelectGovCategoryFilter(cat) },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RadarCyan.copy(alpha = 0.22f),
                                selectedLabelColor = AdaptiveCyanAccent
                            ),
                            modifier = Modifier.testTag("gov_news_filter_${cat.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        items(filteredGovNews, key = { it.id }) { announcement ->
            GovernmentAnnouncementCard(
                announcement = announcement,
                onOpenOfficialUrl = { onOpenOfficialUrl(announcement.officialUrl) }
            )
        }

        // 4. Notification Creation Time Interval Selector inside Safety Hub as well
        item {
            NotificationIntervalSelectorCard(
                selectedNearestCity = selectedNearestCity,
                selectedRegionName = selectedRegion.name,
                selectedInterval = selectedNotificationInterval,
                onSelectInterval = onSelectNotificationInterval
            )
        }

        // 5. 24/7 Sri Lanka Emergency Hotlines Grid
        item {
            Text(
                text = "ONE-TAP NATIONAL EMERGENCY HOTLINES",
                style = MaterialTheme.typography.labelLarge,
                color = AdaptivePrimaryText
            )
        }

        items(hotlines.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { hotline ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, hotline.color.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                            .clickable { onDialNumber(hotline.number) }
                            .testTag("hotline_card_${hotline.number}")
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
                                Text(
                                    text = hotline.number,
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = hotline.color,
                                    fontWeight = FontWeight.Bold
                                )
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Dial ${hotline.number}",
                                    tint = hotline.color,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Text(
                                text = hotline.agency,
                                style = MaterialTheme.typography.titleSmall,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = hotline.role,
                                style = MaterialTheme.typography.bodySmall,
                                color = AdaptiveSecondaryText
                            )
                        }
                    }
                }
            }
        }

        // 6. Citizen Flood & Landslide Readiness Checklist
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, AdaptiveBorderColor, RoundedCornerShape(20.dp))
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
                        Text(
                            text = "HOUSEHOLD FLOOD READINESS",
                            style = MaterialTheme.typography.labelLarge,
                            color = AdaptiveCyanAccent
                        )
                        Text(
                            text = "$completedCount / ${checklist.size} Completed",
                            style = MaterialTheme.typography.labelLarge,
                            color = SafeEmerald
                        )
                    }

                    LinearProgressIndicator(
                        progress = { progress },
                        color = SafeEmerald,
                        trackColor = AdaptiveElevatedColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    checklist.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AdaptiveElevatedColor)
                                .clickable { onToggleChecklistItem(item.id) }
                                .padding(12.dp)
                                .testTag("checklist_item_${item.id}"),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                                contentDescription = item.title,
                                tint = if (item.isChecked) SafeEmerald else AdaptiveCyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Column {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = AdaptivePrimaryText,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = item.detail,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AdaptiveSecondaryText
                                )
                            }
                        }
                    }
                }
            }
        }

        // 7. Designated High-Ground Evacuation Centres
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HomeWork,
                    contentDescription = null,
                    tint = WarningAmber,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "HIGH-GROUND EVACUATION SHELTERS (PRIORITIZED FOR ${selectedRegion.name.uppercase()})",
                    style = MaterialTheme.typography.labelLarge,
                    color = AdaptivePrimaryText
                )
            }
        }

        val sortedShelters = shelters.sortedByDescending {
            it.district.equals(selectedRegion.name, ignoreCase = true)
        }

        items(sortedShelters, key = { it.id }) { shelter ->
            val isLocalMatch = shelter.district.equals(selectedRegion.name, ignoreCase = true)
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isLocalMatch) 1.5.dp else 1.dp,
                        color = if (isLocalMatch) SafeEmerald else AdaptiveBorderColor,
                        shape = RoundedCornerShape(18.dp)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = shelter.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = AdaptivePrimaryText,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${shelter.district} District • Elevation: ${shelter.elevationMeters}m ASL • ${shelter.distanceKmText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = AdaptiveCyanAccent
                            )
                        }

                        if (isLocalMatch) {
                            Surface(
                                color = SafeEmerald.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "NEAREST",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SafeEmerald,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Facilities: ${shelter.facilities.joinToString(" • ")} (Capacity: ${shelter.capacityPeople})",
                        style = MaterialTheme.typography.bodySmall,
                        color = AdaptiveSecondaryText
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { onDialNumber(shelter.contactPhone) },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Call Center (${shelter.contactPhone})")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ResidingAreaOfficialGovernmentCard(
    announcement: GovernmentAnnouncement,
    selectedNearestCity: String,
    selectedDistrict: String,
    onOpenOfficialUrl: () -> Unit
) {
    val badgeColor = announcement.severity.badgeColor
    val isDark = LocalIsDarkTheme.current
    val gradientColors = if (isDark) {
        listOf(Color(0xFF0E2947), Color(0xFF0A1D34), Color(0xFF132642))
    } else {
        listOf(Color(0xFFE0F3FC), Color(0xFFD4ECFA), Color(0xFFE6F2FA))
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.6.dp, RadarCyan, RoundedCornerShape(22.dp))
            .testTag("safety_hub_residing_gov_card")
    ) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(colors = gradientColors))
                .padding(16.dp),
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
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = AdaptiveCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "OFFICIAL GOV DIRECTIVE FOR ${selectedNearestCity.uppercase()}",
                        style = MaterialTheme.typography.labelLarge,
                        color = AdaptiveCyanAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = badgeColor.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = selectedDistrict.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = announcement.headline,
                style = MaterialTheme.typography.titleLarge,
                color = AdaptivePrimaryText,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${announcement.issuingMinistryOrAgency} • Ref: ${announcement.referenceCode}",
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveTealAccent
            )

            Text(
                text = announcement.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = AdaptivePrimaryText
            )

            announcement.keyDirectives.forEach { directive ->
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SafeEmerald,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .size(15.dp)
                    )
                    Text(
                        text = directive,
                        style = MaterialTheme.typography.bodySmall,
                        color = AdaptiveSecondaryText
                    )
                }
            }

            Button(
                onClick = onOpenOfficialUrl,
                colors = ButtonDefaults.buttonColors(
                    containerColor = RadarCyan,
                    contentColor = Color(0xFF041222)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("residing_gov_official_link_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(17.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Access Official News: ${announcement.officialPortalName}",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun GovernmentAnnouncementCard(
    announcement: GovernmentAnnouncement,
    onOpenOfficialUrl: () -> Unit
) {
    var expanded by remember { mutableStateOf(announcement.isResidingAreaMatch) }
    val badgeColor = announcement.severity.badgeColor

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AdaptiveSurfaceColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (announcement.isResidingAreaMatch) 1.5.dp else 1.dp,
                color = if (announcement.isResidingAreaMatch) badgeColor.copy(alpha = 0.7f) else AdaptiveBorderColor,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { expanded = !expanded }
            .testTag("gov_announcement_card_${announcement.id}")
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
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
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(badgeColor)
                    )
                    Text(
                        text = announcement.category.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold
                    )
                    if (announcement.isResidingAreaMatch) {
                        Surface(
                            color = RadarCyan.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "AFFECTS YOUR AREA",
                                style = MaterialTheme.typography.labelMedium,
                                color = AdaptiveCyanAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = AdaptiveSecondaryText
                )
            }

            Text(
                text = announcement.headline,
                style = MaterialTheme.typography.titleMedium,
                color = AdaptivePrimaryText,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${announcement.issuingMinistryOrAgency} • ${announcement.publishedTimeText}",
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveCyanAccent
            )

            Text(
                text = announcement.summary,
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveSecondaryText
            )

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AdaptiveElevatedColor)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "OFFICIAL GOVERNMENT DIRECTIVES (REF: ${announcement.referenceCode})",
                        style = MaterialTheme.typography.labelMedium,
                        color = AdaptiveTealAccent
                    )
                    announcement.keyDirectives.forEach { line ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SafeEmerald,
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(14.dp)
                            )
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall,
                                color = AdaptivePrimaryText
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Source: ${announcement.officialUrl}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AdaptiveSecondaryText,
                    modifier = Modifier.weight(1f)
                )

                OutlinedButton(
                    onClick = onOpenOfficialUrl,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp),
                    modifier = Modifier.testTag("gov_news_link_${announcement.id}")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = announcement.officialPortalName,
                        style = MaterialTheme.typography.labelMedium,
                        color = AdaptiveCyanAccent
                    )
                }
            }
        }
    }
}
