package com.example.myfirstapplication

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myfirstapplication.frontend.AddEditScheduleScreen
import com.example.myfirstapplication.frontend.AllSchedulesScreen
import com.example.myfirstapplication.frontend.HomeScreen
import com.example.myfirstapplication.frontend.ScheduleViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: ScheduleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(viewModel: ScheduleViewModel) {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val notificationManager = remember {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    var hasDndAccess by remember { mutableStateOf(notificationManager.isNotificationPolicyAccessGranted) }

    if (!hasDndAccess) {
        // Simple gate screen until permission granted
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ring Scheduler needs Do Not Disturb access to change your ringer mode automatically.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
            }) { Text("Grant Access") }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                hasDndAccess = notificationManager.isNotificationPolicyAccessGranted
            }) { Text("I've granted it — Continue") }
        }
        return
    }

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onAddClick = { navController.navigate("add_edit") },
                onViewAllClick = { navController.navigate("all_schedules") }
            )
        }
        composable("all_schedules") {
            AllSchedulesScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAddClick = { navController.navigate("add_edit") },
                onEditClick = { id -> navController.navigate("add_edit?scheduleId=$id") }
            )
        }
        composable(
            route = "add_edit?scheduleId={scheduleId}",
            arguments = listOf(navArgument("scheduleId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { backStackEntry ->
            val idArg = backStackEntry.arguments?.getInt("scheduleId") ?: -1
            AddEditScheduleScreen(
                viewModel = viewModel,
                scheduleId = if (idArg == -1) null else idArg,
                onDone = { navController.popBackStack() }
            )
        }
    }
}