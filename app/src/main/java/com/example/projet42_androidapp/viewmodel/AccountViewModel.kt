package com.example.projet42_androidapp.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class AccountViewModel : ViewModel() {
    var isUserLoggedIn = mutableStateOf(false)
    var showLoginForm = mutableStateOf(true)
    var userFirstName = mutableStateOf("")
    var userLastName = mutableStateOf("")
    var userEmail = mutableStateOf("")
    var userPassword = mutableStateOf("")

    fun fetchUserInfo(token: String) {
        val url = "http://192.168.1.29:8080/api/user"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                Log.e("AccountViewModel", "Failed to fetch user info: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("AccountViewModel", "Unsuccessful response: ${response.code}")
                    return
                }

                response.body?.let {
                    val jsonResponse = it.string()
                    Log.d("AccountViewModel", "User info response: $jsonResponse")
                    try {
                        val jsonObject = JSONObject(jsonResponse)

                        // Logging each key to check its presence
                        Log.d("AccountViewModel", "given_name: ${jsonObject.optString("given_name", "N/A")}")
                        Log.d("AccountViewModel", "family_name: ${jsonObject.optString("family_name", "N/A")}")
                        Log.d("AccountViewModel", "email: ${jsonObject.optString("email", "N/A")}")
                        Log.d("AccountViewModel", "name: ${jsonObject.optString("name", "N/A")}")

                        val givenName = jsonObject.optString("given_name", "Unknown")
                        val familyName = jsonObject.optString("family_name", "Unknown")
                        val email = jsonObject.optString("email", "Unknown")

                        userFirstName.value = givenName
                        userLastName.value = familyName
                        userEmail.value = email
                    } catch (e: JSONException) {
                        Log.e("AccountViewModel", "JSON parsing error: ${e.message}")
                    }
                } ?: Log.e("AccountViewModel", "Response body is null")
            }
        })
    }

}





