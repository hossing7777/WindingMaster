package com.windingmaster.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * دیاگرام گسترده سیم‌پیچی (Unrolled / Flat Diagram)
 * شیارها در یک ردیف افقی، کویل‌ها به شکل قوس روی آن‌ها
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

    val slotWidthDp = 45.dp
    val totalWidthDp = (slots * 45 + 100).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5))
            .horizontalScroll(scrollState)
    ) {
        Canvas(
            modifier = Modifier
                .width(totalWidthDp)
                .fillMaxHeight()
        ) {
            val sizeW = size.width
            val sizeH = size.height
            val startX = sizeW * 0.06f
            val endX = sizeW * 0.94f
            val effectiveWidth = endX - startX
            val slotSpacing = effectiveWidth / slots

            val baseY = sizeH * 0.72f
            val topY = sizeH * 0.18f
            val slotLineHalf = 20f

            val phaseColors = listOf(
                Color(0xFFFFC107), // U: زرد
                Color(0xFF4CAF50), // V: سبز
                Color(0xFFF44336), // W: قرمز
                Color(0xFF2196F3)  // تک‌فاز: آبی
            )

            // خط پایه
            drawLine(
                color = Color.DarkGray,
                start = Offset(startX, baseY),
                end = Offset(endX, baseY),
                strokeWidth = 3f
            )

            // شیارها و شماره‌ها
            for (i in 0 until slots) {
                val x = startX + (i + 0.5f) * slotSpacing

                drawLine(
                    color = Color.Black,
                    start = Offset(x, baseY - slotLineHalf),
                    end = Offset(x, baseY + slotLineHalf),
                    strokeWidth = 2f
                )

                val text = textMeasurer.measure(
                    text = "${i + 1}",
                    style = TextStyle(color = Color.Black, fontSize = 10.sp)
                )
                drawText(
                    textLayoutResult = text,
                    topLeft = Offset(x - text.size.width / 2f, baseY + slotLineHalf + 4f)
                )
            }

            // کویل‌ها
            for (i in 0 until slots) {
                val coilStart = startX + (i + 0.5f) * slotSpacing
                val endSlotIndex = (i + coilPitch) % slots
                val coilEnd = startX + (endSlotIndex + 0.5f) * slotSpacing

                val angle = (i * 360.0 * (poles / 2) / slots) % 360.0
                val phaseIndex = when {
                    phases == 1 -> 3
                    angle < 120 -> 0
                    angle < 240 -> 1
                    else -> 2
                }
                val color = phaseColors[phaseIndex]

                val path = Path().apply {
                    moveTo(coilStart, baseY - slotLineHalf)
                    lineTo(coilStart, topY)
                    lineTo(coilEnd, topY)
                    lineTo(coilEnd, baseY - slotLineHalf)
                }
                drawPath(path, color, style = Stroke(width = 3f))

                // فلش جهت جریان
                val arrowY = baseY - slotLineHalf - 6f
                drawLine(
                    color = color,
                    start = Offset(coilStart, arrowY),
                    end = Offset(coilStart + 6f, arrowY - 6f),
                    strokeWidth = 2f
                )
                drawLine(
                    color = color,
                    start = Offset(coilStart, arrowY),
                    end = Offset(coilStart + 6f, arrowY + 6f),
                    strokeWidth = 2f
                )
            }
        }
    }
}
