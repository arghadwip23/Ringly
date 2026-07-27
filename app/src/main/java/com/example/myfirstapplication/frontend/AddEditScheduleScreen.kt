package com.example.myfirstapplication.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun AddEditScheduleScreen(
    viewModel: ScheduleViewModel,
    scheduleId: Int?, // null = adding new
    onDone: () -> Unit
) {
    var dayOfWeek by remember { mutableIntStateOf(Calendar.MONDAY) }
    var hour by remember { mutableIntStateOf(9) }
    var minute by remember { mutableIntStateOf(0) }
    var mode by remember { mutableIntStateOf(MODE_VIBRATE) }
    var label by remember { mutableStateOf("") }
    var enabled by remember { mutableStateOf(true) }
    var existingId by remember { mutableStateOf<Int?>(null) }

    val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute, is24Hour = false)

    LaunchedEffect(scheduleId) {
        if (scheduleId != null) {
            val existing = viewModel.getById(scheduleId)
            if (existing != null) {
                dayOfWeek = existing.dayOfWeek
                hour = existing.hour
                minute = existing.minute
                mode = existing.mode
                label = existing.label
                enabled = existing.enabled
                existingId = existing.id
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (scheduleId == null) "Add Schedule" else "Edit Schedule") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Day", style = MaterialTheme.typography.titleMedium)
            DayPicker(selectedDay = dayOfWeek, onSelect = { dayOfWeek = it })

            Text("Time", style = MaterialTheme.typography.titleMedium)
            TimePicker(state = timePickerState)

            Text("Mode", style = MaterialTheme.typography.titleMedium)
            ModePicker(selectedMode = mode, onSelect = { mode = it })

            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                label = { Text("Label (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text("Enabled", modifier = Modifier.weight(1f))
                Switch(checked = enabled, onCheckedChange = { enabled = it })
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val schedule = Schedule(
                        id = existingId ?: 0,
                        dayOfWeek = dayOfWeek,
                        hour = timePickerState.hour,
                        minute = timePickerState.minute,
                        mode = mode,
                        label = label,
                        enabled = enabled
                    )
                    if (existingId == null) {
                        viewModel.addSchedule(schedule)
                    } else {
                        viewModel.updateSchedule(schedule)
                    }
                    onDone()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
fun DayPicker(selectedDay: Int, onSelect: (Int) -> Unit) {
    val days = listOf(
        Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        days.forEach { day ->
            FilterChip(
                selected = day == selectedDay,
                onClick = { onSelect(day) },
                label = { Text(dayName(day).take(3)) }
            )
        }
    }
}

@Composable
fun ModePicker(selectedMode: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(MODE_RING to "Ring", MODE_VIBRATE to "Vibrate", MODE_SILENT to "Silent").forEach { (m, label) ->
            FilterChip(
                selected = m == selectedMode,
                onClick = { onSelect(m) },
                label = { Text(label) }
            )
        }
    }
}