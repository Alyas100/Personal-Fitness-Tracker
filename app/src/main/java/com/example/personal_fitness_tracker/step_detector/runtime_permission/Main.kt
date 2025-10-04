package com.example.personal_fitness_tracker.step_detector.runtime_permission


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext

class Main : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionScreen()
        }
    }

}


// This function handles the logic for requesting and reacting to the permission status
@Composable
fun PermissionScreen() {
    val context = LocalContext.current

    // 1. Create the Launcher to handle the result
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // 3. The Callback: This block executes when the user responds to the prompt.
        if (isGranted) {
            Log.d("ComposePermission", "ACTIVITY_RECOGNITION granted.")
            // Start your step counter or tracking service here
        } else {
            Log.w("ComposePermission", "ACTIVITY_RECOGNITION denied.")
            // Show a rationale or error message to the user
        }
    }

    // 2. Use SideEffect to check and request permission when the Composable first runs
    // Note: We only need the permission check/request logic for Android Q (API 29) and above.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        SideEffect {
            when {
                // Check if permission is already granted
                context.checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    Log.d("ComposePermission", "Permission already held.")
                }
                // Check if we should show rationale (optional step)
                // shouldShowRequestPermissionRationale is a function of the Activity
                // else if (activity.shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)) {
                //      // Show a dialog explaining why the permission is needed
                // }
                else -> {
                    // Launch the system permission prompt
                    permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            }
        }
    }

    // Simple UI to show the permission status (or a feature)
    Button(onClick = { /* Could launch settings or prompt again */ }) {
        Text("Fitness Tracking Feature")
    }
}