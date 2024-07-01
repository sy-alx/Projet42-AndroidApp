package com.example.projet42_androidapp.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class QrCodeViewModel : ViewModel() {

    fun generateQRCode(content: String, size: Int): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        return bitmap
    }

    fun checkUserRegistration(eventId: Long, userId: String, token: String, callback: (Boolean) -> Unit) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://192.168.1.29:8080/api/events/checkRegistration?eventId=$eventId&userId=$userId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                callback(false)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(false)
                } else {
                    val body = response.body?.string()
                    callback(body?.toBoolean() == true)
                }
            }
        })
    }

    fun showMessage(context: Context, message: String) {
        viewModelScope.launch(Dispatchers.Main) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
