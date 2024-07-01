package com.example.projet42_androidapp.Activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class QRCodeScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure the activity is in portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        startQRCodeScanner()
    }

    private fun startQRCodeScanner() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setBarcodeImageEnabled(false)
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult? = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                setResult(Activity.RESULT_CANCELED)
            } else {
                val intent = Intent().apply {
                    putExtra("SCAN_RESULT", result.contents)
                }
                setResult(Activity.RESULT_OK, intent)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        finish()
    }

    companion object {
        const val REQUEST_CODE_SCAN = 100
    }
}