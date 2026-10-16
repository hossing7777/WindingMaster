package com.windingmaster.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * دیاگرام استاندارد سیم‌پیچی گسترده (Unrolled Winding Diagram)
 *
 * هر شیار با رنگ فاز مربوطه مشخص می‌شود و کویل‌ها به صورت قوس رسم می‌شوند.
 * جهت جریان با فلش نمایش داده می‌شود.
 */
@Composable
fun ExtendedDiagram(
    slots: Int,
    poles: Int,
    phases: Int,
    coilPitch: Int,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val textMeasurer = rememberTextMeasurer()

    val slotSpacingDp = 42.dp
    val totalWidthDp = (slots * 42 + 120).dp
    val phaseColors = listOf(
        Color(0xFF2196F3), // Single-phase main
        Color(0xFFFFC107), // U: زرد
        Color(0xFF4CAF50), // V: سبز
        Color(0xFFE53935), // W: قرمز
        Color(0xFF2196F3)  // تک‌فاز: آبی
    )
    val phaseLabels = if (phases == 3) listOf("U", "V", "W") else listOf("M", "A")

    // رنگ پس‌زمینه
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(360.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFAFAFA))
    ) {
        Box(modifier = Modifier.horizontalScroll(scrollState)) {
            Canvas(
                modifier = Modifier
                    .width(totalWidthDp)
                    .fillMaxHeight()
            ) {
                val sizeW = size.width
                val sizeH = size.height
                val startX = 60f
                val slotSpacing = 42.dp.toPx()
                val baseY = sizeH * 0.62f       // خط شیارها
                val topY = sizeH * 0.18f        // بالای کویل‌ها
                val slotLineHalf = 28f

                // محاسبه اطلاعات هر شیار
                val slotInfo = (0 until slots).map { i ->
                    val angle = (i * 360.0 * (poles / 2) / slots) % 360.0
                    // زاویه نرمالایز شده (0..360)
                    val a = if (angle < 0) angle + 360 else angle

                    // تخصیص فاز بر اساس زاویه (۶۰ درجه پراکندگی)
                    val phase = when {
                        phases == 1 -> 0
                        a < 60 || a >= 180 && a < 240 -> 0
                        a < 120 || a >= 240 && a < 300 -> 1
                        else -> 2
                    }

                    // جهت جریان: نیمه‌ی اول (۰-۱۸۰) → بالا، نیمه‌ی دوم → پایین
                    val direction = if (a < 180) 1 else -1

                    Triple(i, phase, direction)
                }

                // خط پایه‌ی شیارها
                drawLine(
                    color = Color(0xFF424242),
                    start = Offset(startX, baseY),
                    end = Offset(startX + (slots - 1) * slotSpacing, baseY),
                    strokeWidth = 3f
                )

                // رسم هر شیار
                for ((idx, phase, dir) in slotInfo) {
                    val x = startX + idx * slotSpacing
                    val color = phaseColors[phase]

                    // خط عمودی شیار با رنگ فاز
                    drawLine(
                        color = color,
                        start = Offset(x, baseY - slotLineHalf),
                        end = Offset(x, baseY + slotLineHalf),
                        strokeWidth = 3f
                    )

                    // دایره‌ی کوچک در محل اتصال به خط پایه
                    drawCircle(color = color, radius = 4f, center = Offset(x, baseY))

                    // فلش جهت جریان بالای شیار
                    val arrowTipY = if (dir > 0) topY + 20f else baseY - slotLineHalf - 6f
                    val arrowBaseY = if (dir > 0) arrowTipY + 12f else arrowTipY - 12f
                    drawLine(
                        color = color,
                        start = Offset(x, arrowBaseY),
                        end = Offset(x, arrowTipY),
                        strokeWidth = 2.5f
                    )
                    // بال‌های فلش
                    val wingSize = 5f
                    val wingY = if (dir > 0) arrowTipY + wingSize else arrowTipY - wingSize
                    drawLine(color, Offset(x, arrowTipY), Offset(x - wingSize, wingY), 2.5f)
                    drawLine(color, Offset(x, arrowTipY), Offset(x + wingSize, wingY), 2.5f)

                    // شماره‌ی شیار در پایین
                    val text = textMeasurer.measure(
                        text = "${idx + 1}",
                        style = TextStyle(color = Color.Black, fontSize = 11.sp)
                    )
                    drawText(
                        textLayoutResult = text,
                        topLeft = Offset(x - text.size.width / 2f, baseY + slotLineHalf + 4f)
                    )

                    // حرف فاز روی شیار
                    val phaseLabel = textMeasurer.measure(
                        text = phaseLabels[phase],
                        style = TextStyle(
                            color = color,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    drawText(
                        textLayoutResult = phaseLabel,
                        topLeft = Offset(x - phaseLabel.size.width / 2f, baseY + slotLineHalf + 22f)
                    )
                }

                // رسم کویل‌ها (قوس‌های بالای شیارها)
                for ((idx, phase, _) in slotInfo) {
                    val endIdx = (idx + coilPitch) % slots
                    val startXc = startX + idx * slotSpacing
                    val endXc = startX + endIdx * slotSpacing
                    val color = phaseColors[phase]

                    // ارتفاع قوس بر اساس فاصله
                    val arcTopY = topY + (idx % 4) * 6f

                    // رسم مسیر قوس
                    val path = Path().apply {
                        moveTo(startXc, baseY - slotLineHalf)
                        lineTo(startXc, arcTopY)
                        lineTo(endXc, arcTopY)
                        lineTo(endXc, baseY - slotLineHalf)
                    }
                    drawPath(path, color, style = Stroke(width = 3f))

                    // نقطه‌ی اتصال در انتهای کویل
                    drawCircle(color = color, radius = 3f, center = Offset(endXc, baseY - slotLineHalf))
                }

                // برچسب نقاط شروع فازها (U1, V1, W1) در بالای دیاگرام
                val startSlotList = if (phases == 3) listOf(0, (slots / 3), (2 * slots / 3)) else listOf(0)
                startSlotList.forEachIndexed { pIdx, slotIdx ->
                    if (slotIdx < slots) {
                        val x = startX + slotIdx * slotSpacing
                        val label = textMeasurer.measure(
                            text = "${phaseLabels[pIdx]}1",
                            style = TextStyle(
                                color = phaseColors[pIdx],
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        drawText(
                            textLayoutResult = label,
                            topLeft = Offset(x - label.size.width / 2f, topY - 10f)
                        )
                    }
                }
            }
        }
    }
}
