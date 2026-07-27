package com.example.myfirstapplication.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapplication.data.MODE_RING
import com.example.myfirstapplication.data.MODE_SILENT
import com.example.myfirstapplication.data.MODE_VIBRATE
import com.example.myfirstapplication.data.Schedule
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ScheduleViewModel,
    onAddClick: () -> Unit,
    onViewAllClick: () -> Unit
) {
    val schedules by viewModel.allSchedules.collectAsState()
    val today = remember { Calendar.getInstance().get(Calendar.DAY_OF_WEEK) }
    val todaySchedules = schedules.filter { it.dayOfWeek == today }
        .sortedWith(compareBy({ it.hour }, { it.minute }))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ringly",
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick,modifier = Modifier.padding(bottom = 87.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Add schedule")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Today's Schedule", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            if (todaySchedules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No schedules for today.\nTap + to add one.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(todaySchedules) { schedule ->
                        ScheduleCard(schedule = schedule, onToggle = { viewModel.toggleEnabled(schedule) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(onClick = onViewAllClick, modifier = Modifier.fillMaxWidth()) {
                Text("View All Schedules")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Made by Arghadwip",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ScheduleCard(schedule: Schedule, onToggle: () -> Unit) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val timeStr = String.format("%02d:%02d", schedule.hour, schedule.minute)
                val modeStr = when (schedule.mode) {
                    MODE_RING -> "Ring"
                    MODE_VIBRATE -> "Vibrate"
                    MODE_SILENT -> "Silent"
                    else -> "Unknown"
                }
                Text(timeStr, style = MaterialTheme.typography.titleMedium)
                Text(modeStr, style = MaterialTheme.typography.bodyMedium)
                if (schedule.label.isNotBlank()) {
                    Text(schedule.label, style = MaterialTheme.typography.bodySmall)
                }
            }
            Switch(checked = schedule.enabled, onCheckedChange = { onToggle() })
        }
    }
}

fun dayName(dayOfWeek: Int): String = when (dayOfWeek) {
    Calendar.SUNDAY -> "Sunday"
    Calendar.MONDAY -> "Monday"
    Calendar.TUESDAY -> "Tuesday"
    Calendar.WEDNESDAY -> "Wednesday"
    Calendar.THURSDAY -> "Thursday"
    Calendar.FRIDAY -> "Friday"
    Calendar.SATURDAY -> "Saturday"
    else -> "Unknown"
}