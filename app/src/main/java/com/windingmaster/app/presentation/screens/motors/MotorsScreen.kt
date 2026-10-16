package com.windingmaster.app.presentation.screens.motors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.windingmaster.app.R
import com.windingmaster.app.domain.engine.WindingCalculator
import com.windingmaster.app.domain.engine.WindingResult
import com.windingmaster.app.presentation.components.ExtendedDiagram
import com.windingmaster.app.presentation.components.WindingDiagram

enum class WindingType(val labelFa: String, val labelEn: String) {
    FULL_PITCH("گام کامل", "Full Pitch"),
    CONCENTRIC("متحدالمرکز", "Concentric"),
    SERIES("سری", "Series"),
    FRACTIONAL("کوتاه‌شده", "Fractional Pitch")
}

@Composable
fun MotorsScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        stringResource(R.string.tab_single_phase),
        stringResource(R.string.tab_three_phase)
    )

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }
        if (selectedTabIndex == 0) {
            MotorInputForm(isThreePhase = false)
        } else {
            MotorInputForm(isThreePhase = true)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorInputForm(isThreePhase: Boolean) {
    var slots by remember { mutableStateOf("24") }
    var poles by remember { mutableStateOf("4") }
    var frequency by remember { mutableStateOf("50") }
    var coilPitch by remember { mutableStateOf("6") }
    var windingType by remember { mutableStateOf(WindingType.FULL_PITCH) }
    var windingTypeExpanded by remember { mutableStateOf(false) }

    val calculator = remember { WindingCalculator() }
    var result by remember { mutableStateOf<WindingResult?>(null) }
    var diagramType by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = slots,
            onValueChange = { slots = it },
            label = { Text(stringResource(R.string.input_slots)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = poles,
            onValueChange = { poles = it },
            label = { Text(stringResource(R.string.input_poles)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            label = { Text("فرکانس (Hz)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = coilPitch,
            onValueChange = { coilPitch = it },
            label = { Text("گام کویل (مثال: 1-8 یا 6)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // منوی انتخاب نوع سیم‌بندی
        ExposedDropdownMenuBox(
            expanded = windingTypeExpanded,
            onExpandedChange = { windingTypeExpanded = !windingTypeExpanded }
        ) {
            OutlinedTextField(
                value = windingType.labelFa,
                onValueChange = {},
                readOnly = true,
                label = { Text("نوع سیم‌بندی") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = windingTypeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = windingTypeExpanded,
                onDismissRequest = { windingTypeExpanded = false }
            ) {
                WindingType.values().forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.labelFa) },
                        onClick = {
                            windingType = type
                            windingTypeExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                result = calculator.calculate(
                    slots.toIntOrNull() ?: 24,
                    poles.toIntOrNull() ?: 4,
                    if (isThreePhase) 3 else 1,
                    frequency.toIntOrNull() ?: 50
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_calculate))
        }

        Spacer(modifier = Modifier.height(16.dp))

        result?.let { res ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ResultRow("گام قطبی (T)", "${res.polePitch}")
                    ResultRow("زاویه شیار (AEZ)", "${String.format("%.2f", res.electricalAngle)}°")
                    ResultRow("شیار در هر قطب در هر فاز (q)", "${String.format("%.2f", res.slotsPerPolePerPhase)}")
                    ResultRow("گام سیم‌بندی (g)", "${res.windingStep}")
                    ResultRow("گام کوتاه‌شده (g')", "${res.windingStepReduced}")
                    ResultRow("گام اول کویل (Y1)", "${res.coilPitchY1}")
                    ResultRow("گام دوم کویل (Y2)", "${res.coilPitchY2}")
                    ResultRow("سرعت سنکرون", "${res.synchronousSpeed} RPM")
                    if (isThreePhase && res.phaseStarts.size == 3) {
                        ResultRow("شروع فازها", "U=${res.phaseStarts[0]}, V=${res.phaseStarts[1]}, W=${res.phaseStarts[2]}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = diagramType == 0,
                    onClick = { diagramType = 0 },
                    label = { Text(stringResource(R.string.diagram_circular)) }
                )
                FilterChip(
                    selected = diagramType == 1,
                    onClick = { diagramType = 1 },
                    label = { Text(stringResource(R.string.diagram_extended)) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            when (diagramType) {
                0 -> WindingDiagram(
                    slots = slots.toIntOrNull() ?: 24,
                    poles = poles.toIntOrNull() ?: 4
                )
                1 -> ExtendedDiagram(
                    slots = slots.toIntOrNull() ?: 24,
                    poles = poles.toIntOrNull() ?: 4,
                    phases = if (isThreePhase) 3 else 1,
                    coilPitch = coilPitch.toIntOrNull() ?: 6
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
}
