package com.example.personal_fitness_tracker.step_detector.preference_datastore

// CREATE EXTENSION PROPERTY FOR FOR CONTEXT
// THIS IS DATASTORE INSTANCE ACT AS SINGLETON, MEANT TO BE ACCESIBLE FROM ANYWHERE IN THE APP
// AND MAKE SURE ONLY ONE INSTANCE CREATED

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Define the DataStore name as a constant
private const val STEP_PREFERENCES_NAME = "step_preferences"

// Create the DataStore instance using a lazy property delegation
// This ensures only one instance is ever created.
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = STEP_PREFERENCES_NAME
)