package com.windingmaster.app.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * دیاگرام گسترده سیم‌پیچی (Unrolled / Flat Diagram)
 * سیم‌پیچی را به صورت تخت نشان می‌دهد: شیارها در یک ردیف افقی، کویل‌ها به شکل قوس
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

    // عرض هر شیار بر حسب پیکسل (برای اسکرول افقی)
    val slotWidthPx = 40f
    val totalWidth = slots * slotWidthPx + 100f
    val diagramHeight = 400f

    val phaseColors = listOf(
        Color(0xFFFFC107), // U: زرد
        Color(0xFF4CAF50), // V: سبز
        Color(0xFFF44336), // W: قرمز
        Color(0xFF2196F3)  // فاز کمکی (تک‌فاز): آبی
    )

    Box(modifier = modifier.horizontalScroll(scrollState)) {
        Canvas(
            modifier = Modifier
                .width(totalWidth.dp)
                .height(diagramHeight.dp)
        ) {
            val baseY = size.height * 0.7f
            val topY = size.height * 0.15f
            val slotLineHeight = 25f
            val startX = 50f

            // رسم خط پایه (خط شیارها)
            drawLine(
                color = Color.Black,
                start = Offset(startX, baseY),
                end = Offset(startX + slots * slotWidthPx, baseY),
                strokeWidth = 2f
            )

            // رسم شیارها و شماره‌گذاری
            for (i in 0 until slots) {
                val x = startX + (i + 0.5f) * slotWidthPx

                // خط عمودی شیار
                drawLine(
                    color = Color.Black,
                    start = Offset(x, baseY - slotLineHeight / 2),
                    end = Offset(x, baseY + slotLineHeight / 2),
                    strokeWidth = 2f
                )

                // شماره شیار زیر خط
                val textLayout = textMeasurer.measure(
                    text = (i + 1).toString(),
                    style = TextStyle(color = Color.Black, fontSize = 11.sp)
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        x - textLayout.size.width / 2,
                        baseY + slotLineHeight
                    )
                )
            }

            // رسم کویل‌ها به شکل قوس
            for (i in 0 until slots) {
                val startX1 = startX + (i + 0.5f) * slotWidthPx
                val endSlot = (i + coilPitch) % slots
                val endX1 = startX + (endSlot + 0.5f) * slotWidthPx

                // تعیین فاز بر اساس زاویه الکتریکی
                val angle = ((i) * 360.0 * (poles / 2) / slots) % 360.0
                val phaseIndex = when {
                    phases == 1 -> 3 // فاز کمکی
                    angle < 120 -> 0
                    angle < 240 -> 1
                    else -> 2
                }
                val color = phaseColors[phaseIndex]

                // رسم قوس کویل
                val path = Path().apply {
                    moveTo(startX1, baseY - slotLineHeight / 2)
                    lineTo(startX1, topY + 30f)
                    lineTo(endX1, topY + 30f)
                    lineTo(endX1, baseY - slotLineHeight / 2)
                }
                drawPath(path, color, style = Stroke(width = 3f))

                // فلش جهت جریان در ابتدای کویل
                val arrowSize = 8f
                drawLine(
                    color = color,
                    start = Offset(startX1, baseY - slotLineHeight / 2),
                    end = Offset(startX1 - arrowSize, baseY - slotLineHeight / 2 - arrowSize),
                    strokeWidth = 2f
                )
                drawLine(
                    color = color,
                    start = Offset(startX1, baseY - slotLineHeight / 2),
                    end = Offset(startX1 + arrowSize, baseY - slotLineHeight / 2 - arrowSize),
                    strokeWidth = 2f
                )
            }

            // رسم برچسب فازها (U, V, W) در بالای دیاگرام
            val labels = if (phases == 3) listOf("U", "V", "W") else listOf("Main", "Aux")
            labels.forEachIndexed { idx, label ->
                val textLayout = textMeasurer.measure(
                    text = label,
                    style = TextStyle(color = phaseColors[idx], fontSize = 14.sp)
                )
                drawText(
                    textLayoutResult = textLayout,
                    topLeft = Offset(
                        10f,
                        topY + idx * 25f
                    )
                )
            }
        }
    }
}
