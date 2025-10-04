package com.example.personal_fitness_tracker.step_detector.sensor


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.viewModelScope
import com.example.personal_fitness_tracker.step_detector.preference_datastore.dataStore
import com.example.personal_fitness_tracker.step_detector.runtime_permission.PermissionScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Define the key for saving the baseline steps
private val BASELINE_STEPS_KEY = floatPreferencesKey("baseline_steps")

// viewmodel here, important for state to survive after configuration changes like screen rotation etc
// it saves the state in dedicated memory area when activity is destroyed on configuration change, then return back to new activity instance that created
class StepViewModel(private val context: Context) : androidx.lifecycle.ViewModel() {
    // State to hold the live step count for the UI
    var currentSteps by mutableStateOf(0)
        private set

    // Property to hold the value loaded from DataStore
    var baselineSteps: Float? = null

    init {
        // Load the baseline steps when the ViewModel is created
        loadBaselineSteps()
    }

    // LOADING DATA: Asynchronously load the baseline steps
    private fun loadBaselineSteps() = viewModelScope.launch {
        baselineSteps = context.dataStore.data
            // .first() ensures we only read the latest value once
            .first()[BASELINE_STEPS_KEY]
    }

    // SAVING DATA: Asynchronously save the baseline steps
    fun saveBaselineSteps(steps: Float) = viewModelScope.launch {
        context.dataStore.edit { preferences ->
            preferences[BASELINE_STEPS_KEY] = steps
        }

        Log.d("DataStore", "SUCCESS: Baseline steps saved: $steps")

    }

    // UPDATING STEPS (Called by SensorActivity)
    fun updateSteps(newSteps: Int) {
        currentSteps = newSteps
    }

}

class SensorActivity : ComponentActivity(), SensorEventListener {
    // NOTE: Renamed class to SensorActivity to avoid conflict with android.hardware.Sensor

    private lateinit var sensorManager: SensorManager
    private var stepCounterSensor: Sensor? = null

    // Last reported value from sensor
    private var baselineSteps: Float? = null

    // use the custom factory to instantiate viewmodel
    private val viewModel: StepViewModel by viewModels {
        StepViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SensorManager and sensor object
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        setContent {
            // this func Check for permission
            // it pass the state from the viewmodel to the composable
            FitnessAppWithSteps(viewModel = viewModel)
        }
    }

    override fun onResume() {
        super.onResume()

        // 🚨 CRITICAL FIX: Only register the listener if the permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
            == PackageManager.PERMISSION_GRANTED) {

            stepCounterSensor?.also { sensor ->
                sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Always unregister when pausing to save battery
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val count = event.values[0]  // cumulative since reboot

            // 🌟 LOGIC FIX: Check if baseline is NOT set or NOT loaded yet
            if (viewModel.baselineSteps == null) {
                // Save the current cumulative count to DataStore as the new baseline
                viewModel.saveBaselineSteps(count)
                viewModel.baselineSteps = count // Update ViewModel property instantly
            }

            // Use the baseline stored in the ViewModel (or 0f if still somehow null)
            val stepsToday = (count - (viewModel.baselineSteps ?: 0f)).toInt()


            //update the viewmodel instead of activity
            viewModel.updateSteps(stepsToday)

            Log.d("StepDetector", "Steps: $stepsToday")

            // 💡 TODO: Send stepsToday to Compose UI using a StateFlow or ViewModel
            Log.d("StepDetector", "Steps: $stepsToday")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Ignored for step counter
    }
}


@Composable
fun FitnessAppWithSteps(viewModel: StepViewModel) {
    val context = LocalContext.current

    // Check if permission is already granted by the actual user on their device
    val isPermissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACTIVITY_RECOGNITION
    ) == PackageManager.PERMISSION_GRANTED

    if (isPermissionGranted) {
        // DISPLAY THE SENSOR THING: Use the passed 'steps' value from the state
        Text("Total Steps Today: ${viewModel.currentSteps}")

    } else {
        // If not granted, show the permission request screen
        PermissionScreen()
    }
}