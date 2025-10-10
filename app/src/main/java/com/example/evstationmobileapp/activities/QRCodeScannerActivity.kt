package com.example.evstationmobileapp.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.QRCodeData
import com.example.evstationmobileapp.models.QRValidationRequest
import com.example.evstationmobileapp.models.QRValidationResponse
import com.example.evstationmobileapp.remote.ApiClient
import com.example.evstationmobileapp.utils.SessionManager
import com.google.gson.Gson
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import kotlinx.coroutines.launch

/**
 * Activity for scanning QR codes to validate bookings
 */
class QRCodeScannerActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var sessionManager: SessionManager
    private var isScanning = false

    // 1. Add a companion object for the permission request code
    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_scanner)

        initializeViews()
        setupToolbar()
        setupBarcodeScanner()
    }

    private fun initializeViews() {
        barcodeView = findViewById(R.id.barcodeView)
        sessionManager = SessionManager(this)
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Scan QR Code"
    }

    private fun setupBarcodeScanner() {
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                if (!isScanning) {
                    isScanning = true
                    handleQRCodeResult(result.text)
                }
            }
            override fun possibleResultPoints(resultPoints: MutableList<com.google.zxing.ResultPoint>?) {}
        })
    }

    // 2. Add this new function to check for permission
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            // If permission is already granted, resume the scanner
            barcodeView.resume()
        } else {
            // Otherwise, request the permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    // 3. Add this function to handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted by the user, resume the scanner
                barcodeView.resume()
            } else {
                // Permission was denied by the user
                Toast.makeText(this, "Camera permission is required to scan QR codes", Toast.LENGTH_LONG).show()
                finish() // Close the scanner activity if permission is denied
            }
        }
    }

    private fun handleQRCodeResult(qrCodeText: String) {
        try {
            // Parse the QR code JSON data
            val gson = Gson()
            val qrData = gson.fromJson(qrCodeText, QRCodeData::class.java)

            // Validate the QR code with the backend
            validateQRCodeWithBackend(qrData)

        } catch (e: Exception) {
            Toast.makeText(this, "Invalid QR code format: ${e.message}", Toast.LENGTH_SHORT).show()
            isScanning = false
        }
    }

    private fun validateQRCodeWithBackend(qrData: QRCodeData) {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error. Please log in.", Toast.LENGTH_SHORT).show()
            isScanning = false
            return
        }

        // Create validation request
        val validationRequest = QRValidationRequest(
            bookingId = qrData.bookingId,
            evOwnerNIC = qrData.evOwnerNIC,
            stationId = qrData.stationId,
            reservationDate = qrData.reservationDate
        )

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.validateQRCode("Bearer $token", validationRequest)
                if (response.isSuccessful && response.body() != null) {
                    val validationResult = response.body()!!
                    handleValidationResult(validationResult)
                } else {
                    Toast.makeText(this@QRCodeScannerActivity,
                        "Validation failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    isScanning = false
                }
            } catch (e: Exception) {
                Toast.makeText(this@QRCodeScannerActivity,
                    "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
                isScanning = false
            }
        }
    }

    private fun handleValidationResult(result: QRValidationResponse) {
        if (result.isValid) {
            // Show success message and booking details
            showValidationSuccess(result)
        } else {
            // Show error message
            showValidationError(result)
        }
        isScanning = false
    }

    private fun showValidationSuccess(result: QRValidationResponse) {
        val bookingDetails = result.bookingDetails
        if (bookingDetails != null) {
            val message = """
                QR Code Valid!
                
                Booking ID: ${bookingDetails.id}
                Station: ${bookingDetails.stationName}
                Status: ${bookingDetails.status}
                
                ${result.message}
            """.trimIndent()

            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "QR Code is valid: ${result.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showValidationError(result: QRValidationResponse) {
        val errorMessage = when (result.errorCode) {
            "BOOKING_NOT_FOUND" -> "Booking not found in the system"
            "INVALID_CUSTOMER" -> "Invalid customer information"
            "INVALID_STATION" -> "Invalid station information"
            "INVALID_STATUS" -> "Booking is not in a valid state for charging"
            "BOOKING_NOT_ACTIVE" -> "Booking is not yet active"
            "BOOKING_EXPIRED" -> "Booking has expired"
            else -> result.message
        }

        Toast.makeText(this, "Validation Failed: $errorMessage", Toast.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
