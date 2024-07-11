package com.example.projet42_androidapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException

class EventSummaryViewModel : ViewModel() {

    data class EventSummary(
        val id: Long,
        val name: String,
        val address: String,
        val status: String,
        val eventDate: String,
        val eventTime: String
    )

    private val _events = MutableStateFlow<List<EventSummary>>(emptyList())
    val events: StateFlow<List<EventSummary>> = _events



    init {
        fetchEvents()
    }

    fun fetchEvents() {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("http://172.20.10.4:8080/api/events/eventsSummarize")
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    Log.e("EventSummaryViewModel", "Failed to fetch events: ${e.message}")
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (!response.isSuccessful) {
                        Log.e("EventSummaryViewModel", "Unsuccessful response: ${response.code}")
                        return
                    }

                    response.body?.let {
                        val jsonResponse = it.string()
                        try {
                            val jsonArray = JSONArray(jsonResponse)
                            val eventsList = mutableListOf<EventSummary>()

                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val event = EventSummary(
                                    id = jsonObject.getLong("id"),
                                    name = jsonObject.getString("name"),
                                    address = jsonObject.getString("address"),
                                    status = jsonObject.getString("status"),
                                    eventDate = jsonObject.getString("eventDate"),
                                    eventTime = jsonObject.getString("eventTime")
                                )
                                eventsList.add(event)
                            }

                            _events.value = eventsList
                        } catch (e: JSONException) {
                            Log.e("EventSummaryViewModel", "JSON parsing error: ${e.message}")
                        }
                    } ?: run {
                        Log.e("EventSummaryViewModel", "Response body is null")
                    }
                }
            })
        }
    }


}
