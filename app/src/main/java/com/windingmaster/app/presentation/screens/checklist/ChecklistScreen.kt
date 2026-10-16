package com.windingmaster.app.presentation.screens.checklist
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun ChecklistScreen() {
    val steps = listOf("باز کردن موتور", "ثبت اطلاعات", "تمیز کردن هسته", "عایق‌بندی", "سیم‌پیچی", "قرار دادن کویل‌ها", "اتصال", "لحیم‌کاری", "تست", "لاک زدن", "مونتاژ نهایی")
    var checkedSteps by remember { mutableStateOf(setOf<Int>()) }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("چک‌لیست تعمیر موتور", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(steps.size) { index ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Checkbox(checked = checkedSteps.contains(index), onCheckedChange = { checkedSteps = if (it) checkedSteps + index else checkedSteps - index })
                    Text(steps[index], modifier = Modifier.padding(start = 8.dp, top = 12.dp))
                }
            }
        }
        Text("پیشرفت: ${(checkedSteps.size * 100 / steps.size)}%", modifier = Modifier.padding(top = 16.dp))
    }
}
