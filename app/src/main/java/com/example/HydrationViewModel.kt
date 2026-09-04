package com.example

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HydrationViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences = application.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _uiState = MutableStateFlow(HydrationUiState())
    val uiState: StateFlow<HydrationUiState> = _uiState.asStateFlow()

    init {
        loadPersistedState()
    }

    private fun loadPersistedState() {
        val currentMl = prefs.getInt(KEY_CURRENT_ML, 0)
        val targetMl = prefs.getInt(KEY_TARGET_ML, 2000)
        val streak = prefs.getInt(KEY_STREAK, 1)
        val historyJson = prefs.getString(KEY_HISTORY_JSON, "[]") ?: "[]"
        val history = parseHistory(historyJson)

        _uiState.update {
            it.copy(
                currentMl = currentMl,
                targetMl = targetMl,
                streakDays = streak,
                history = history
            )
        }
    }

    fun addWater(amountMl: Int = 250) {
        val newEntry = HydrationEntry(amountMl = amountMl)
        _uiState.update { state ->
            val updatedMl = (state.currentMl + amountMl).coerceAtLeast(0)
            val updatedHistory = listOf(newEntry) + state.history
            state.copy(
                currentMl = updatedMl,
                history = updatedHistory
            )
        }
        persistState()
    }

    fun reset() {
        _uiState.update { state ->
            state.copy(
                currentMl = 0,
                history = emptyList()
            )
        }
        persistState()
    }

    fun undoLast() {
        _uiState.update { state ->
            if (state.history.isEmpty()) return@update state
            val lastEntry = state.history.first()
            val newHistory = state.history.drop(1)
            val newMl = (state.currentMl - lastEntry.amountMl).coerceAtLeast(0)
            state.copy(
                currentMl = newMl,
                history = newHistory
            )
        }
        persistState()
    }

    fun setTarget(targetMl: Int) {
        if (targetMl <= 0) return
        _uiState.update { it.copy(targetMl = targetMl) }
        persistState()
    }

    private fun persistState() {
        viewModelScope.launch {
            val state = _uiState.value
            val historyJson = serializeHistory(state.history)
            prefs.edit()
                .putInt(KEY_CURRENT_ML, state.currentMl)
                .putInt(KEY_TARGET_ML, state.targetMl)
                .putInt(KEY_STREAK, state.streakDays)
                .putString(KEY_HISTORY_JSON, historyJson)
                .apply()
        }
    }

    private fun serializeHistory(history: List<HydrationEntry>): String {
        val array = JSONArray()
        history.take(30).forEach { entry ->
            val obj = JSONObject()
            obj.put("id", entry.id)
            obj.put("amountMl", entry.amountMl)
            obj.put("timestamp", entry.timestamp)
            array.put(obj)
        }
        return array.toString()
    }

    private fun parseHistory(json: String): List<HydrationEntry> {
        val list = mutableListOf<HydrationEntry>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.getLong("id")
                val amount = obj.getInt("amountMl")
                val timestamp = obj.getLong("timestamp")
                list.add(HydrationEntry(id = id, amountMl = amount, timestamp = timestamp))
            }
        } catch (_: Exception) {
            // fallback gracefully
        }
        return list
    }

    companion object {
        private const val PREFS_NAME = "hydration_tracker_prefs"
        private const val KEY_CURRENT_ML = "key_current_ml"
        private const val KEY_TARGET_ML = "key_target_ml"
        private const val KEY_STREAK = "key_streak"
        private const val KEY_HISTORY_JSON = "key_history_json"
    }
}
