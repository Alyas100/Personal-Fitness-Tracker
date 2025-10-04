package com.example.personal_fitness_tracker


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitnessApp()
        }
    }
}

// Bottom menu items
sealed class BottomNavItem(val title: String, val icon: ImageVector) {
    object Home : BottomNavItem("Home", Icons.Filled.DirectionsRun)
    object Workouts : BottomNavItem("Workouts", Icons.Filled.FitnessCenter)
    object Profile : BottomNavItem("Profile", Icons.Filled.Person)
}

@Composable
fun FitnessApp() {
    var selectedItem by remember { mutableStateOf<BottomNavItem>(BottomNavItem.Home) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Workouts,
                    BottomNavItem.Profile
                ).forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedItem) {
                is BottomNavItem.Home -> HomePage()
                is BottomNavItem.Workouts -> WorkoutsPage()
                is BottomNavItem.Profile -> ProfilePage()
            }
        }
    }
}

@Composable
fun HomePage() {
    var steps by remember { mutableStateOf(3500) } // mock current steps
    val dailyGoal = 10000
    val caloriesBurned = steps * 0.04
    val progress = (steps.toFloat() / dailyGoal).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Daily Summary
        Text(
            text = "Today's Progress",
            fontSize = 24.sp,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Key Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(label = "Steps", value = "$steps")
            StatCard(label = "Calories", value = "${"%.0f".format(caloriesBurned)} kcal")
            StatCard(label = "Goal", value = "$dailyGoal")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Progress Bar
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${(progress * 100).toInt()}% of daily goal",
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Quick actions
        Text(text = "Quick Actions", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { steps += 500 }) {
                Text("Simulate Steps")
            }
            Button(onClick = { steps = 0 }) {
                Text("Reset")
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, style = MaterialTheme.typography.bodyLarge)
        Text(text = label, fontSize = 14.sp, style = MaterialTheme.typography.bodyMedium)
    }
}



@Composable
fun WorkoutsPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Workouts Page - Start Exercises", fontSize = 20.sp)
    }
}

@Composable
fun ProfilePage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Profile Page - Personal Stats", fontSize = 20.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FitnessApp()
}