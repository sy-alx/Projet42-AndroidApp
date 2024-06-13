package com.example.projet42_androidapp.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.projet42_androidapp.MainActivity
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretBasic
import net.openid.appauth.ResponseTypeValues
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

object AuthConfig {
    private const val AUTH_ENDPOINT = "http://192.168.1.29:8090/realms/projet42-realm/protocol/openid-connect/auth"
    private const val TOKEN_ENDPOINT = "http://192.168.1.29:8090/realms/projet42-realm/protocol/openid-connect/token"
    private const val CLIENT_ID = "projet42-api"
    private const val CLIENT_SECRET = "UjttrpYWQb78I0wVtlxTc3bHJnDM0zqc"
    private const val REDIRECT_URI = "projet42://callback"

    fun createAuthRequest(context: Context): AuthorizationRequest {
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(AUTH_ENDPOINT),
            Uri.parse(TOKEN_ENDPOINT)
        )

        return AuthorizationRequest.Builder(
            serviceConfig,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(REDIRECT_URI)
        ).setScopes("openid", "profile", "email")
            .build()
    }

    fun createAuthService(context: Context): AuthorizationService {
        return AuthorizationService(context)
    }

    fun createClientAuthentication(): ClientAuthentication {
        return ClientSecretBasic(CLIENT_SECRET)
    }

    fun logout(context: Context, token: String, refreshToken: String, onSuccess: () -> Unit, onError: () -> Unit) {
        val url = "http://192.168.1.29:8090/realms/projet42-realm/protocol/openid-connect/logout"

        val client = OkHttpClient()
        val body = FormBody.Builder()
            .add("client_id", CLIENT_ID)
            .add("client_secret", CLIENT_SECRET)
            .add("refresh_token", refreshToken)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
                (context as MainActivity).runOnUiThread {
                    onError()
                }
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                if (!response.isSuccessful) {
                    Log.e("AuthConfig", "Failed to logout: ${response.code}")
                    (context as MainActivity).runOnUiThread {
                        onError()
                    }
                } else {
                    (context as MainActivity).runOnUiThread {
                        onSuccess()
                    }
                }
            }
        })
    }
}
