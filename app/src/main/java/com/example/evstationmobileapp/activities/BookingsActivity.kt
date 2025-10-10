package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.adapters.BookingAdapter
import com.example.evstationmobileapp.models.Booking
import com.example.evstationmobileapp.remote.ApiClient
import com.example.evstationmobileapp.utils.SessionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class BookingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var fabAddBooking: FloatingActionButton
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_bookings)

        initializeViews()
        setupRecyclerView()
        setupFAB()

        fetchUserBookings()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewBookings)
        fabAddBooking = findViewById(R.id.fabAddBooking)
        sessionManager = SessionManager(this)
    }

    private fun setupRecyclerView() {
        bookingAdapter = BookingAdapter(
            bookings = emptyList(),
            onViewQRClick = { booking ->
                // Navigate to QR code display activity
                val intent = Intent(this, QRCodeDisplayActivity::class.java)
                intent.putExtra("booking", booking)
                startActivity(intent)
            },
            onCancelClick = { booking ->
                showCancelConfirmationDialog(booking)
            }
        )

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BookingsActivity)
            adapter = bookingAdapter
        }
    }

    private fun fetchUserBookings() {
        val token = sessionManager.fetchAuthToken()

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getMyBookings("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    bookingAdapter.updateBookings(response.body()!!.bookings)
                } else {
                    Toast.makeText(this@BookingsActivity, "Failed to load bookings: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookingsActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showCancelConfirmationDialog(booking: Booking) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Booking")
            .setMessage("Are you sure you want to cancel your booking at ${booking.stationName}?")
            .setPositiveButton("Yes, Cancel") { _, _ ->
                performCancelBooking(booking.id)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performCancelBooking(bookingId: String) {
        val token = sessionManager.fetchAuthToken()
        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Authentication error.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.cancelBooking("Bearer $token", bookingId)
                if (response.isSuccessful) {
                    Toast.makeText(this@BookingsActivity, "Booking cancelled successfully.", Toast.LENGTH_SHORT).show()
                    // Refresh the list to show the updated status
                    fetchUserBookings()
                } else {
                    Toast.makeText(this@BookingsActivity, "Failed to cancel booking: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@BookingsActivity, "Network Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupFAB() {
        fabAddBooking.setOnClickListener {
            // Navigate to the stations list to start a new booking
            val intent = Intent(this, StationsActivity::class.java)
            startActivity(intent)
        }
    }
}