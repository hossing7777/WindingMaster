package com.windingmaster.app.presentation.screens.experiences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun ExperiencesScreen() {
    var text by remember { mutableStateOf("") }
    val notes = remember { mutableStateListOf<String>() }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("تجربیات من", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("ثبت تجربه جدید...") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { if (text.isNotBlank()) { notes.add(text); text = "" } }, modifier = Modifier.fillMaxWidth()) { Text("افزودن") }
        Spacer(modifier = Modifier.height(16.dp))
        notes.forEach { note -> Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) { Text(note, modifier = Modifier.padding(16.dp)) } }
    }
}
