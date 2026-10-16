package com.windingmaster.app.presentation.screens.motors
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.windingmaster.app.presentation.components.WindingDiagram
import com.windingmaster.app.domain.engine.WindingCalculator
@Composable
fun MotorsScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("موتور تک‌فاز", "موتور سه‌فاز")
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title -> Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }, text = { Text(title) }) }
        }
        if (selectedTabIndex == 0) MotorInputForm(isThreePhase = false) else MotorInputForm(isThreePhase = true)
    }
}
@Composable
fun MotorInputForm(isThreePhase: Boolean) {
    var slots by remember { mutableStateOf("24") }
    var poles by remember { mutableStateOf("4") }
    val calculator = remember { WindingCalculator() }
    var result by remember { mutableStateOf<com.windingmaster.app.domain.engine.WindingResult?>(null) }
    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        OutlinedTextField(value = slots, onValueChange = { slots = it }, label = { Text("تعداد شیارها (Z)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = poles, onValueChange = { poles = it }, label = { Text("تعداد قطب‌ها (2p)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { result = calculator.calculate(slots.toIntOrNull() ?: 24, poles.toIntOrNull() ?: 4, if (isThreePhase) 3 else 1) }, modifier = Modifier.fillMaxWidth()) { Text("محاسبه و رسم دیاگرام") }
        Spacer(modifier = Modifier.height(16.dp))
        result?.let { res ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("گام قطبی: ${res.polePitch}")
                    Text("زاویه شیار: ${String.format("%.2f", res.slotAngleElectrical)}°")
                    Text("شیار در هر قطب در هر فاز: ${String.format("%.2f", res.slotsPerPolePerPhase)}")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            WindingDiagram(slots = slots.toIntOrNull() ?: 24, poles = poles.toIntOrNull() ?: 4)
        }
    }
}
