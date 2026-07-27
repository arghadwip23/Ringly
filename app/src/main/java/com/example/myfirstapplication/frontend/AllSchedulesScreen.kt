package com.example.myfirstapplication.frontend


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myfirstapplication.data.MODE_RING
import com.example.myfirstapplication.data.MODE_SILENT
import com.example.myfirstapplication.data.MODE_VIBRATE
import com.example.myfirstapplication.data.Schedule
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSchedulesScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Int) -> Unit
) {
    val schedules by viewModel.allSchedules.collectAsState()
    val grouped = schedules
        .sortedWith(compareBy({ it.dayOfWeek }, { it.hour }, { it.minute }))
        .groupBy { it.dayOfWeek }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Schedules") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add schedule")
            }
        }
    ) { padding ->
        if (schedules.isEmpty()) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No schedules yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { (day, list) ->
                    item {
                        Text(
                            dayName(day),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    }
                    items(list) { schedule ->
                        EditableScheduleCard(
                            schedule = schedule,
                            onToggle = { viewModel.toggleEnabled(schedule) },
                            onClick = { onEditClick(schedule.id) },
                            onDelete = { viewModel.deleteSchedule(schedule) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditableScheduleCard(
    schedule: Schedule,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}