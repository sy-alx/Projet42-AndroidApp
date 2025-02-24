package com.example.projet42_androidapp.viewmodel

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.auth0.jwt.JWT
import com.auth0.jwt.interfaces.DecodedJWT
import com.example.projet42_androidapp.utils.AuthConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.math.log


class AccountViewModel : ViewModel() {
    var isUserLoggedIn = mutableStateOf(false)
    var showLoginForm = mutableStateOf(true)
    var userFirstName = mutableStateOf("")
    var userLastName = mutableStateOf("")
    var userEmail = mutableStateOf("")
    var userPassword = mutableStateOf("")
    var isEditing = mutableStateOf(false)
    private var authToken: String? = null
    var refreshToken: String? = null
    var isAdmin = mutableStateOf(false)
    var userSub = mutableStateOf<String?>(null)

    fun initializeTokens(token: String?, refreshToken: String?) {
        this.authToken = token
        this.refreshToken = refreshToken
        isUserLoggedIn.value = token != null
        checkIfAdmin()
        extractUserSub()
    }

    fun getAuthToken(): String? {
        return authToken
    }

    private fun checkIfAdmin() {
        authToken?.let { token ->
            try {
                val jwt: DecodedJWT = JWT.decode(token)
                val resourceAccessClaim = jwt.getClaim("resource_access").asMap()
                Log.d("AccountViewModel", "resource_access claim: $resourceAccessClaim")

                val projet42Api = resourceAccessClaim["projet42-api"] as? Map<*, *>
                val roles = projet42Api?.get("roles") as? List<*>

                isAdmin.value = roles?.contains("ADMIN") ?: false

                Log.d("AccountViewModel", "Roles extracted: $roles")
            } catch (e: Exception) {
                Log.e("AccountViewModel", "Error decoding JWT: ${e.message}")
                isAdmin.value = false
            }
        }
    }

    private fun extractUserSub() {
        authToken?.let {
            try {
                val parts = it.split(".")
                if (parts.size == 3) {
                    val payload = String(Base64.decode(parts[1], Base64.URL_SAFE))
                    val json = JSONObject(payload)
                    userSub.value = json.getString("sub")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun toggleEditMode() {
        isEditing.value = !isEditing.value
    }

    fun register(username: String, firstName: String, lastName: String, email: String, password: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val url = "http://172.20.10.4:8080/api/user/register"
        val client = OkHttpClient()

        val jsonBody = JSONObject()
        jsonBody.put("username", username)
        jsonBody.put("firstName", firstName)
        jsonBody.put("lastName", lastName)
        jsonBody.put("email", email)
        jsonBody.put("password", password)

        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBody.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onError(e.message ?: "An unknown error occurred")
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    onError("Unsuccessful response: ${response.code}")
                    return
                }

                response.body?.let {
                    val jsonResponse = it.string()
                    try {
                        val jsonObject = JSONObject(jsonResponse)
                        val message = jsonObject.optString("message", "Unknown error")
                        if (message.contains("successfully")) {
                            onSuccess()
                        } else {
                            onError(message)
                        }
                    } catch (e: JSONException) {
                        onError("JSON parsing error: ${e.message}")
                    }
                } ?: run {
                    onError("Response body is null")
                }
            }
        })
    }

    fun updateEmail(newEmail: String, context: Context, onLogoutClick: () -> Unit) {
        authToken?.let { token ->
            val url = "http://172.20.10.4:8080/api/user/email"
            val client = OkHttpClient()

            val jsonBody = JSONObject()
            jsonBody.put("newEmail", newEmail)
            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                jsonBody.toString()
            )

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.e("AccountViewModel", "Failed to update email: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        Log.e("AccountViewModel", "Unsuccessful response: ${response.code}")
                        return
                    }

                    response.body?.let {
                        val jsonResponse = it.string()
                        Log.d("AccountViewModel", "Update email response: $jsonResponse")
                        try {
                            val jsonObject = JSONObject(jsonResponse)
                            val message = jsonObject.optString("message", "Unknown error")
                            Log.d("AccountViewModel", "Response message: $message")

                            // Déconnecter l'utilisateur en cas de changement d'email
                            if (message.contains("successfully")) {
                                Log.d("AccountViewModel", "Email updated, logging out user")
                                if (refreshToken != null) {
                                    AuthConfig.logout(
                                        context = context,
                                        token = token,
                                        refreshToken = refreshToken!!,
                                        onSuccess = {
                                            isUserLoggedIn.value = false
                                            Log.d("AccountViewModel", "User logged out successfully")
                                            onLogoutClick() // Navigate to login screen or perform necessary UI updates
                                        },
                                        onError = {
                                            Log.e("AccountViewModel", "Error logging out user")
                                        }
                                    )
                                } else {
                                    Log.e("AccountViewModel", "refreshToken is null, cannot log out user")
                                }
                            } else {
                                Log.e("AccountViewModel", "Error updating email: $message")
                            }
                        } catch (e: JSONException) {
                            Log.e("AccountViewModel", "JSON parsing error: ${e.message}")
                        }
                    } ?: run {
                        Log.e("AccountViewModel", "Response body is null")
                    }
                }
            })
        } ?: run {
            Log.e("AccountViewModel", "authToken is null, cannot update email")
        }
    }

    fun updatePassword(newPassword: String, context: Context, onLogoutClick: () -> Unit) {
        authToken?.let { token ->
            val url = "http://172.20.10.4:8080/api/user/password"
            val client = OkHttpClient()

            val jsonBody = JSONObject()
            jsonBody.put("newPassword", newPassword)
            val requestBody = RequestBody.create(
                "application/json; charset=utf-8".toMediaTypeOrNull(),
                jsonBody.toString()
            )

            val request = Request.Builder()
                .url(url)
                .put(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.e("AccountViewModel", "Failed to update password: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        Log.e("AccountViewModel", "Unsuccessful response: ${response.code}")
                        return
                    }

                    response.body?.let {
                        val jsonResponse = it.string()
                        Log.d("AccountViewModel", "Update password response: $jsonResponse")
                        try {
                            val jsonObject = JSONObject(jsonResponse)
                            val message = jsonObject.optString("message", "Unknown error")
                            Log.d("AccountViewModel", "Response message: $message")

                            // Déconnecter l'utilisateur en cas de changement de mot de passe
                            if (message.contains("successfully")) {
                                Log.d("AccountViewModel", "Password updated, logging out user")
                                if (refreshToken != null) {
                                    AuthConfig.logout(
                                        context = context,
                                        token = token,
                                        refreshToken = refreshToken!!,
                                        onSuccess = {
                                            isUserLoggedIn.value = false
                                            Log.d("AccountViewModel", "User logged out successfully")
                                            onLogoutClick() // Navigate to login screen or perform necessary UI updates
                                        },
                                        onError = {
                                            Log.e("AccountViewModel", "Error logging out user")
                                        }
                                    )
                                } else {
                                    Log.e("AccountViewModel", "refreshToken is null, cannot log out user")
                                }
                            } else {
                                Log.e("AccountViewModel", "Error updating password: $message")
                            }
                        } catch (e: JSONException) {
                            Log.e("AccountViewModel", "JSON parsing error: ${e.message}")
                        }
                    } ?: run {
                        Log.e("AccountViewModel", "Response body is null")
                    }
                }
            })
        } ?: run {
            Log.e("AccountViewModel", "authToken is null, cannot update password")
        }
    }

    fun fetchUserInfo(token: String) {
        authToken = token
        val url = "http://172.20.10.4:8080/api/user"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                Log.e("AccountViewModel", "Failed to fetch user info: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
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
                } ?: run {
                    Log.e("AccountViewModel", "Response body is null")
                }
            }
        })
    }

    fun registerToEvent(eventId: Long, file: File, onSuccess: () -> Unit, onError: (String) -> Unit) {
        authToken?.let { token ->
            val url = "http://172.20.10.4:8080/api/events/$eventId/register"
            val client = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.name, file.asRequestBody("application/pdf".toMediaTypeOrNull()))
                .build()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onError(e.message ?: "An unknown error occurred")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onError("Unsuccessful response: ${response.code}")
                        return
                    }
                    onSuccess()
                }
            })
        } ?: run {
            onError("User is not authenticated")
        }
    }

    fun isRegisteredToEvent(eventId: Long, onResult: (Boolean) -> Unit) {
        authToken?.let { token ->
            val url = "http://172.20.10.4:8080/api/events/$eventId/isRegistered"
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onResult(false)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onResult(false)
                        return
                    }

                    val isRegistered = response.body?.string()?.toBoolean() ?: false
                    onResult(isRegistered)
                }
            })
        } ?: run {
            onResult(false)
        }
    }

    fun unregisterFromEvent(eventId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        authToken?.let { token ->
            val url = "http://172.20.10.4:8080/api/events/$eventId/unregister"
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(url)
                .delete()
                .addHeader("Authorization", "Bearer $token")
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onError(e.message ?: "An unknown error occurred")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        onError("Unsuccessful response: ${response.code}")
                        return
                    }
                    onSuccess()
                }
            })
        } ?: run {
            onError("User is not authenticated")
        }
    }

}
