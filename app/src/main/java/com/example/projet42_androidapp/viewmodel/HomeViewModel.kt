package com.example.projet42_androidapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class HomeViewModel : ViewModel() {
    private val _nearestEvent = MutableStateFlow<EventSummary?>(null)
    val nearestEvent: StateFlow<EventSummary?> = _nearestEvent

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered: StateFlow<Boolean> = _isRegistered

    fun fetchNearestEvent() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://192.168.1.29:8080/api/events/nearestUpcomingEvent")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val json = JSONObject(responseBody.string())
                        val eventSummary = EventSummary(
                            id = json.getLong("id"),
                            name = json.getString("name"),
                            address = json.getString("address"),
                            status = json.getString("status"),
                            eventDate = json.getString("eventDate"),
                            eventTime = json.getString("eventTime")
                        )
                        _nearestEvent.value = eventSummary
                        Log.d("HomeViewModel", "Nearest event fetched: $eventSummary")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("HomeViewModel", "Error fetching nearest event: ${e.message}")
            }
        }
    }

    fun checkIfUserIsRegistered(eventId: Long, accountViewModel: AccountViewModel) {
        accountViewModel.isRegisteredToEvent(eventId) { isRegistered ->
            _isRegistered.value = isRegistered
            Log.d("HomeViewModel", "User registration status for event $eventId: $isRegistered")
        }
    }

    fun setRegistered(isRegistered: Boolean) {
        _isRegistered.value = isRegistered
    }

    data class EventSummary(
        val id: Long,
        val name: String,
        val address: String,
        val status: String,
        val eventDate: String,
        val eventTime: String
    )
}
