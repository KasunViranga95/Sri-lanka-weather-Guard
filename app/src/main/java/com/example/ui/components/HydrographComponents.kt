package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.HydrographTimeRange
import com.example.model.RiverGaugeStation
import com.example.ui.theme.AdaptiveBorderColor
import com.example.ui.theme.AdaptiveCyanAccent
import com.example.ui.theme.AdaptiveSecondaryText
import com.example.ui.theme.AdaptiveSurfaceColor
import com.example.ui.theme.AdaptiveTealAccent
import com.example.ui.theme.AdvisoryYellow
import com.example.ui.theme.AlertRed
import com.example.ui.theme.RadarCyan
import com.example.ui.theme.WarningAmber
import java.util.Locale

@Composable
fun RiverThresholdBar(
    station: RiverGaugeStation,
    modifier: Modifier = Modifier
) {
    val maxScale = (station.majorFloodLevelM * 1.18).coerceAtLeast(station.currentLevelM * 1.05)
    val alertFrac = (station.alertLevelM / maxScale).toFloat().coerceIn(0f, 1f)
    val minorFrac = (station.minorFloodLevelM / maxScale).toFloat().coerceIn(0f, 1f)
    val majorFrac = (station.majorFloodLevelM / maxScale).toFloat().coerceIn(0f, 1f)
    val currentFrac = (station.currentLevelM / maxScale).toFloat().coerceIn(0.04f, 1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color(0xFF071426))
        ) {
            val w = size.width
            val h = size.height

            drawRoundRect(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        RadarCyan,
                        station.floodStatus.color
                    )
                ),
                size = Size(w * currentFrac, h),
                cornerRadius = CornerRadius(9.dp.toPx(), 9.dp.toPx())
            )

            val markers = listOf(
                alertFrac to AdvisoryYellow,
                minorFrac to WarningAmber,
                majorFrac to AlertRed
            )
            markers.forEach { (frac, markerColor) ->
                val x = w * frac
                drawLine(
                    color = markerColor,
                    start = Offset(x, 0f),
                    end = Offset(x, h),
                    strokeWidth = 2.5.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThresholdLabel(
                title = "Alert",
                valueM = station.alertLevelM,
                color = AdvisoryYellow,
                modifier = Modifier.weight(1f)
            )
            ThresholdLabel(
                title = "Minor",
                valueM = station.minorFloodLevelM,
                color = WarningAmber,
                modifier = Modifier.weight(1f)
            )
            ThresholdLabel(
                title = "Major",
                valueM = station.majorFloodLevelM,
                color = AlertRed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThresholdLabel(
    title: String,
    valueM: Double,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .padding(end = 4.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
                .padding(horizontal = 3.dp)
        )
        Text(
            text = "$title: ${String.format(Locale.US, "%.2fm", valueM)}",
            style = MaterialTheme.typography.labelSmall,
            color = AdaptiveSecondaryText,
            maxLines = 1
        )
    }
}

@Composable
fun HydrographTimeRangeSelector(
    selectedRange: HydrographTimeRange,
    onRangeSelected: (HydrographTimeRange) -> Unit,
    stationIdTagPrefix: String = "global",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HydrographTimeRange.entries.forEach { range ->
            val isSelected = range == selectedRange
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) RadarCyan.copy(alpha = 0.22f) else AdaptiveSurfaceColor
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) RadarCyan else AdaptiveBorderColor,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onRangeSelected(range) }
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .testTag("hydrograph_range_${stationIdTagPrefix}_${range.id}")
            ) {
                Text(
                    text = range.shortLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) AdaptiveCyanAccent else AdaptiveSecondaryText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun RiverHydrographCurve(
    station: RiverGaugeStation,
    timeRange: HydrographTimeRange = HydrographTimeRange.HOURS_24,
    modifier: Modifier = Modifier
) {
    val points = station.getLevelsForTimeRange(timeRange)
    val minPoint = points.minOrNull() ?: station.previousLevelM
    val maxPoint = points.maxOrNull() ?: station.currentLevelM
    val avgPoint = if (points.isNotEmpty()) points.average() else station.currentLevelM

    val minVal = (minOf(minPoint, station.alertLevelM) * 0.85)
    val maxVal = (maxOf(maxPoint, station.majorFloodLevelM) * 1.08)
    val range = (maxVal - minVal).coerceAtLeast(0.5)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Min: ${String.format(Locale.US, "%.2fm", minPoint)}",
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveSecondaryText
            )
            Text(
                text = "Avg (${timeRange.shortLabel}): ${String.format(Locale.US, "%.2fm", avgPoint)}",
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveTealAccent
            )
            Text(
                text = "Peak: ${String.format(Locale.US, "%.2fm", maxPoint)}",
                style = MaterialTheme.typography.labelMedium,
                color = station.floodStatus.color,
                fontWeight = FontWeight.Bold
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF071527))
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            val w = size.width
            val h = size.height

            val majorY = h - ((station.majorFloodLevelM - minVal) / range).toFloat().coerceIn(0f, 1f) * h
            drawLine(
                color = AlertRed.copy(alpha = 0.55f),
                start = Offset(0f, majorY),
                end = Offset(w, majorY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )

            val minorY = h - ((station.minorFloodLevelM - minVal) / range).toFloat().coerceIn(0f, 1f) * h
            drawLine(
                color = WarningAmber.copy(alpha = 0.55f),
                start = Offset(0f, minorY),
                end = Offset(w, minorY),
                strokeWidth = 1.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
            )

            val alertY = h - ((station.alertLevelM - minVal) / range).toFloat().coerceIn(0f, 1f) * h
            drawLine(
                color = AdvisoryYellow.copy(alpha = 0.40f),
                start = Offset(0f, alertY),
                end = Offset(w, alertY),
                strokeWidth = 1.2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
            )

            val linePath = Path()
            val fillPath = Path()

            points.forEachIndexed { idx, level ->
                val x = if (points.size > 1) (idx.toFloat() / (points.size - 1)) * w else w / 2f
                val y = h - ((level - minVal) / range).toFloat().coerceIn(0f, 1f) * h
                if (idx == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, h)
                    fillPath.lineTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
                if (idx == points.lastIndex) {
                    fillPath.lineTo(x, h)
                    fillPath.close()
                }
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        station.floodStatus.color.copy(alpha = 0.35f),
                        Color.Transparent
                    )
                )
            )
            drawPath(
                path = linePath,
                color = station.floodStatus.color,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            points.forEachIndexed { idx, level ->
                val x = if (points.size > 1) (idx.toFloat() / (points.size - 1)) * w else w / 2f
                val y = h - ((level - minVal) / range).toFloat().coerceIn(0f, 1f) * h
                drawCircle(
                    color = station.floodStatus.color,
                    radius = if (idx == points.lastIndex) 5.dp.toPx() else 3.5.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = timeRange.xAxisStartLabel,
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveSecondaryText
            )
            Text(
                text = timeRange.xAxisMidLabel,
                style = MaterialTheme.typography.bodySmall,
                color = AdaptiveSecondaryText
            )
            Text(
                text = "${timeRange.xAxisEndLabel} (${String.format(Locale.US, "%.2fm", station.currentLevelM)})",
                style = MaterialTheme.typography.labelMedium,
                color = AdaptiveCyanAccent,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
