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
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
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
        if (selectedTabIndex == 0) MotorInputForm(isThreePhase = false)
        else MotorInputForm(isThreePhase = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MotorInputForm(isThreePhase: Boolean) {
    var slots by remember { mutableStateOf("24") }
    var poles by remember { mutableStateOf("4") }
    var frequency by remember { mutableStateOf("50") }
    // گام کویل به صورت اتوماتیک محاسبه می‌شود: Z / 2p
    var coilPitch by remember { mutableStateOf("6") }
    var windingType by remember { mutableStateOf(0) } // 0..3
    var windingTypeExpanded by remember { mutableStateOf(false) }

    val windingTypeLabels = listOf(
        stringResource(R.string.winding_full_pitch),
        stringResource(R.string.winding_concentric),
        stringResource(R.string.winding_series),
        stringResource(R.string.winding_fractional)
    )

    val calculator = remember { WindingCalculator() }
    var result by remember { mutableStateOf<WindingResult?>(null) }
    var diagramType by remember { mutableIntStateOf(0) }

    // محاسبه‌ی خودکار گام کویل هر بار که slots یا poles تغییر کند
    LaunchedEffect(slots, poles) {
        val s = slots.toIntOrNull() ?: 0
        val p = poles.toIntOrNull() ?: 0
        if (s > 0 && p > 0 && s % p == 0) {
            coilPitch = (s / p).toString()
        }
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = slots,
            onValueChange = { slots = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.input_slots)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = poles,
            onValueChange = { poles = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.input_poles)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.input_frequency)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = coilPitch,
            onValueChange = { coilPitch = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.input_coil_pitch)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = false
        )

        Spacer(modifier = Modifier.height(12.dp))

        ExposedDropdownMenuBox(
            expanded = windingTypeExpanded,
            onExpandedChange = { windingTypeExpanded = !windingTypeExpanded }
        ) {
            OutlinedTextField(
                value = windingTypeLabels[windingType],
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.input_winding_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = windingTypeExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable)
            )
            ExposedDropdownMenu(
                expanded = windingTypeExpanded,
                onDismissRequest = { windingTypeExpanded = false }
            ) {
                windingTypeLabels.forEachIndexed { idx, label ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            windingType = idx
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
                    ResultRow(stringResource(R.string.result_pole_pitch), "${res.polePitch}")
                    ResultRow(stringResource(R.string.result_slot_angle), "${String.format("%.2f", res.electricalAngle)}°")
                    ResultRow(stringResource(R.string.result_slots_per_pole_per_phase), "${String.format("%.2f", res.slotsPerPolePerPhase)}")
                    ResultRow(stringResource(R.string.result_winding_step), "${res.windingStep}")
                    ResultRow(stringResource(R.string.result_winding_step_reduced), "${res.windingStepReduced}")
                    ResultRow(stringResource(R.string.result_coil_pitch_y1), "${res.coilPitchY1}")
                    ResultRow(stringResource(R.string.result_coil_pitch_y2), "${res.coilPitchY2}")
                    ResultRow(stringResource(R.string.result_synchronous_speed), "${res.synchronousSpeed} RPM")
                    if (isThreePhase && res.phaseStarts.size == 3) {
                        ResultRow(
                            stringResource(R.string.result_phase_starts),
                            "U=${res.phaseStarts[0]}, V=${res.phaseStarts[1]}, W=${res.phaseStarts[2]}"
                        )
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

            val s = slots.toIntOrNull() ?: 24
            val p = poles.toIntOrNull() ?: 4
            val cp = coilPitch.toIntOrNull() ?: max(1, s / p)

            when (diagramType) {
                0 -> WindingDiagram(slots = s, poles = p)
                1 -> ExtendedDiagram(
                    slots = s,
                    poles = p,
                    phases = if (isThreePhase) 3 else 1,
                    coilPitch = cp
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
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
