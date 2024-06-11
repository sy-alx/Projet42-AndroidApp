// CustomConnectionBuilder.kt
package com.example.projet42_androidapp.utils

import android.net.Uri
import net.openid.appauth.connectivity.ConnectionBuilder
import java.net.HttpURLConnection
import java.net.URL

object CustomConnectionBuilder : ConnectionBuilder {
    override fun openConnection(uri: Uri): HttpURLConnection {
        val url = URL(uri.toString())
        return url.openConnection() as HttpURLConnection
    }
}
