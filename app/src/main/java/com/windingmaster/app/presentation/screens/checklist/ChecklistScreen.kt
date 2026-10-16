package com.windingmaster.app.presentation.screens.checklist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.windingmaster.app.R

@Composable
fun ChecklistScreen() {
    val steps = listOf(
        stringResource(R.string.checklist_step_1),
        stringResource(R.string.checklist_step_2),
        stringResource(R.string.checklist_step_3),
        stringResource(R.string.checklist_step_4),
        stringResource(R.string.checklist_step_5),
        stringResource(R.string.checklist_step_6),
        stringResource(R.string.checklist_step_7),
        stringResource(R.string.checklist_step_8),
        stringResource(R.string.checklist_step_9),
        stringResource(R.string.checklist_step_10),
        stringResource(R.string.checklist_step_11)
    )
    var checkedSteps by remember { mutableStateOf(setOf<Int>()) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(R.string.checklist_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(steps.size) { index ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Checkbox(
                        checked = checkedSteps.contains(index),
                        onCheckedChange = {
                            checkedSteps = if (it) checkedSteps + index else checkedSteps - index
                        }
                    )
                    Text(steps[index], modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                }
            }
        }
        val progress = if (steps.isNotEmpty()) checkedSteps.size * 100 / steps.size else 0
        Text(
            text = "${stringResource(R.string.checklist_progress)}: $progress%",
            modifier = Modifier.padding(top = 16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}
