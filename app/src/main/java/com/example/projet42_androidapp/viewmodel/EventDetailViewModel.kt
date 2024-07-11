package com.example.projet42_androidapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projet42_androidapp.viewmodels.EventSummaryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class EventDetailViewModel : ViewModel() {
    private val _eventDetails = MutableStateFlow<EventDetails?>(null)
    val eventDetails: StateFlow<EventDetails?> = _eventDetails

    fun fetchEventDetails(eventId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://172.20.10.4:8080/api/events/eventDetails/$eventId")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.let { responseBody ->
                        val json = JSONObject(responseBody.string())
                        val eventDetails = EventDetails(
                            id = json.getLong("id"),
                            name = json.getString("name"),
                            description = json.getString("description"),
                            address = json.getString("address"),
                            route = json.getString("route"),
                            eventDate = json.getString("eventDate"),
                            eventTime = json.getString("eventTime"),
                            status = json.getJSONObject("status").getString("name"),
                            sports = json.getJSONArray("sports").let { sportsArray ->
                                List(sportsArray.length()) { index ->
                                    sportsArray.getJSONObject(index).getString("name")
                                }
                            }
                        )
                        _eventDetails.value = eventDetails
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace() // Log the error or handle it as necessary
            }
        }
    }

    fun updateEventDate(eventId: Long, newDate: String, token: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val json = JSONObject().put("eventDate", newDate)
                val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://172.20.10.4:8080/api/events/$eventId/updateEventDate")
                    .patch(requestBody)
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun updateEventTime(eventId: Long, newTime: String, token: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val json = JSONObject().put("eventTime", newTime)
                val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://172.20.10.4:8080/api/events/$eventId/updateEventTime")
                    .patch(requestBody)
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    fun updateEventStatus(eventId: Long, newStatusId: Long, token: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val json = JSONObject().put("statusId", newStatusId)
                val requestBody = json.toString().toRequestBody("application/json".toMediaTypeOrNull())
                val request = Request.Builder()
                    .url("http://172.20.10.4:8080/api/events/$eventId/updateEventStatus")
                    .patch(requestBody)
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError(response.message)
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

    data class EventDetails(
        val id: Long,
        val name: String,
        val description: String,
        val address: String,
        val route: String,
        val eventDate: String,
        val eventTime: String,
        val status: String,
        val sports: List<String>
    )
}

