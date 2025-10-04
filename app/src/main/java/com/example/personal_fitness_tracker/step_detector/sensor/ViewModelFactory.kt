package com.example.personal_fitness_tracker.step_detector.sensor

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class StepViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StepViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StepViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}