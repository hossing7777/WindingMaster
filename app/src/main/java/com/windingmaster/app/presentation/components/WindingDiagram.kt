package com.windingmaster.app.presentation.components
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
@Composable
fun WindingDiagram(slots: Int, poles: Int, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxWidth().aspectRatio(1f)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 * 0.8f
        drawCircle(color = Color.Gray, radius = radius, center = center, style = Stroke(width = 4f))
        val angleStep = 2 * PI / slots
        for (i in 0 until slots) {
            val angle = i * angleStep - PI / 2
            val startX = center.x + (radius - 20f) * cos(angle).toFloat()
            val startY = center.y + (radius - 20f) * sin(angle).toFloat()
            val endX = center.x + radius * cos(angle).toFloat()
            val endY = center.y + radius * sin(angle).toFloat()
            drawLine(Color.Black, Offset(startX, startY), Offset(endX, endY), strokeWidth = 2f)
        }
        val polePitch = slots / poles
        for (i in 0 until slots step polePitch) {
            val angle1 = i * angleStep - PI / 2
            drawArc(color = Color(0xFF6200EE), startAngle = Math.toDegrees(angle1).toFloat(), sweepAngle = Math.toDegrees(angleStep * polePitch).toFloat(), useCenter = false, topLeft = Offset(center.x - radius * 0.7f, center.y - radius * 0.7f), size = androidx.compose.ui.geometry.Size(radius * 1.4f, radius * 1.4f), style = Stroke(width = 3f))
        }
    }
}
