package com.example.evstationmobileapp.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.Booking
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Activity for displaying QR code for a booking
 */
class QRCodeDisplayActivity : AppCompatActivity() {

    private lateinit var qrCodeImageView: ImageView
    private lateinit var bookingNumberText: TextView
    private lateinit var stationNameText: TextView
    private lateinit var reservationTimeText: TextView
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_display)

        initializeViews()
        setupToolbar()

        // Get booking data from intent - THIS IS THE FIX
        val booking = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("booking", Booking::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<Booking>("booking")
        }

        if (booking != null) {
            displayBookingInfo(booking)
            generateAndDisplayQRCode(booking)
        } else {
            Toast.makeText(this, "Booking information not available", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initializeViews() {
        qrCodeImageView = findViewById(R.id.qrCodeImageView)
        bookingNumberText = findViewById(R.id.bookingNumberText)
        stationNameText = findViewById(R.id.stationNameText)
        reservationTimeText = findViewById(R.id.reservationTimeText)
        statusText = findViewById(R.id.statusText)
    }

    private fun setupToolbar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Booking QR Code"
    }

    private fun displayBookingInfo(booking: Booking) {
        bookingNumberText.text = "Booking ID: ${booking.id}"
        stationNameText.text = "Station: ${booking.stationName}"

        // Format reservation time
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
            val date = inputFormat.parse(booking.reservationStartTime)
            reservationTimeText.text = "Reservation: ${outputFormat.format(date ?: Date())}"
        } catch (e: Exception) {
            reservationTimeText.text = "Reservation: ${booking.reservationStartTime}"
        }

        statusText.text = "Status: ${booking.status}"
    }

    private fun generateAndDisplayQRCode(booking: Booking) {
        try {
            // Create QR code data
            val qrData = createQRCodeData(booking)

            // Generate QR code bitmap
            val qrBitmap = generateQRCodeBitmap(qrData)

            // Display the QR code
            qrCodeImageView.setImageBitmap(qrBitmap)

        } catch (e: Exception) {
            Toast.makeText(this, "Error generating QR code: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createQRCodeData(booking: Booking): String {
        // Create JSON string with booking data
        val qrData = """
            {
                "bookingId": "${booking.id}",
                "evOwnerNIC": "NIC_PLACEHOLDER",
                "stationId": "STATION_ID_PLACEHOLDER",
                "reservationDate": "${booking.reservationStartTime}"
            }
        """.trimIndent()

        return qrData
    }

    private fun generateQRCodeBitmap(data: String): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix: BitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
            null
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
