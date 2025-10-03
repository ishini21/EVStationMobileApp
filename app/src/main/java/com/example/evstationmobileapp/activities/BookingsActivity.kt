package com.example.evstationmobileapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.adapters.BookingAdapter
import com.example.evstationmobileapp.models.Booking
import com.example.evstationmobileapp.models.BookingStatus
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BookingsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookingAdapter: BookingAdapter
    private lateinit var fabAddBooking: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_bookings)

        // Initialize views first
        initializeViews()

        // Setup RecyclerView with data
        setupRecyclerView()

        // Setup FAB
        setupFAB()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.recyclerViewBookings)
        fabAddBooking = findViewById(R.id.fabAddBooking)

        Log.d("BookingsActivity", "Views initialized")
    }

    private fun setupRecyclerView() {
        // Sample data - Replace with your backend data
        val sampleBookings = listOf(
            Booking(
                id = "1",
                stationName = "Keels EV",
                location = "Kandana",
                date = "01/01/2025",
                time = "2.00 Pm",
                status = BookingStatus.PENDING
            ),
            Booking(
                id = "2",
                stationName = "Keels EV",
                location = "Kandana",
                date = "01/01/2025",
                time = "2.00 Pm",
                status = BookingStatus.COMPLETED
            ),
            Booking(
                id = "3",
                stationName = "Keels EV",
                location = "Kandana",
                date = "01/01/2025",
                time = "2.00 Pm",
                status = BookingStatus.PENDING
            ),
            Booking(
                id = "4",
                stationName = "Keels EV",
                location = "Kandana",
                date = "01/01/2025",
                time = "2.00 Pm",
                status = BookingStatus.ONGOING
            ),
            Booking(
                id = "5",
                stationName = "Keels EV",
                location = "Kandana",
                date = "02/01/2025",
                time = "3.00 Pm",
                status = BookingStatus.CANCELED
            )
        )

        Log.d("BookingsActivity", "Sample bookings created: ${sampleBookings.size} items")

        // Create adapter
        bookingAdapter = BookingAdapter(
            bookings = sampleBookings,
            onViewQRClick = { booking ->
                Toast.makeText(this, "View QR for ${booking.stationName}", Toast.LENGTH_SHORT).show()
            },
            onCancelClick = { booking ->
                Toast.makeText(this, "Cancel booking for ${booking.stationName}", Toast.LENGTH_SHORT).show()
            }
        )

        Log.d("BookingsActivity", "Adapter created with ${bookingAdapter.itemCount} items")

        // Setup RecyclerView
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@BookingsActivity)
            adapter = bookingAdapter
        }

        Log.d("BookingsActivity", "RecyclerView setup complete")
    }

    private fun setupFAB() {
        fabAddBooking.setOnClickListener {
            Toast.makeText(this, "Add new booking", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to add booking screen
        }
    }
}