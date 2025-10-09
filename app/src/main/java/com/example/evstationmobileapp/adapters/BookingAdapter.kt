package com.example.evstationmobileapp.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.Booking
import com.google.android.material.button.MaterialButton
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class BookingAdapter(
    private var bookings: List<Booking>,
    private val onViewQRClick: (Booking) -> Unit,
    private val onCancelClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvStationName: TextView = itemView.findViewById(R.id.tvStationName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val layoutButtons: LinearLayout = itemView.findViewById(R.id.layoutButtons)
        val btnViewQR: MaterialButton = itemView.findViewById(R.id.btnViewQR)
        val btnCancel: MaterialButton = itemView.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        // Ensure you are using the correct layout file name here
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_card, parent, false)
        return BookingViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        holder.tvStationName.text = booking.stationName
        holder.tvStatus.text = booking.status
        // The API's booking object doesn't include location, so we hide this view
        holder.tvLocation.visibility = View.GONE

        // Parse the ISO 8601 date string from the API
        try {
            val zonedDateTime = ZonedDateTime.parse(booking.reservationStartTime)
            val dateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.getDefault())
            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
            holder.tvDate.text = zonedDateTime.format(dateFormatter)
            holder.tvTime.text = zonedDateTime.format(timeFormatter)
        } catch (e: Exception) {
            holder.tvDate.text = "Invalid Date"
            holder.tvTime.text = ""
        }

        // Set status text and background based on the string from the API
        val statusDrawableRes = when (booking.status.lowercase(Locale.ROOT)) {
            "confirmed" -> R.drawable.bg_status_confirmed
            "completed" -> R.drawable.bg_status_completed
            "cancelled" -> R.drawable.bg_status_cancelled
            "ongoing" -> R.drawable.bg_status_ongoing
            else -> R.drawable.bg_status_pending // Default for "Pending" or other statuses
        }
        holder.tvStatus.background = ContextCompat.getDrawable(holder.itemView.context, statusDrawableRes)

        // Determine if the booking can be cancelled
        val isCancellable = booking.status.equals("Confirmed", ignoreCase = true) ||
                booking.status.equals("Pending", ignoreCase = true)

        holder.btnCancel.visibility = if (isCancellable) View.VISIBLE else View.GONE
        // The buttons container might need adjustment depending on your final layout logic
        // For simplicity, we can control the whole container or individual buttons.
        // holder.layoutButtons.visibility = if(isCancellable) View.VISIBLE else View.GONE
        // Note: The above line would hide the QR button too. Hiding only the cancel button is often better.


        // Set button click listeners
        holder.btnViewQR.setOnClickListener {
            onViewQRClick(booking)
        }

        holder.btnCancel.setOnClickListener {
            onCancelClick(booking)
        }
    }

    override fun getItemCount(): Int = bookings.size

    // Add this function to allow the activity to update the list of bookings
    fun updateBookings(newBookings: List<Booking>) {
        this.bookings = newBookings
        notifyDataSetChanged()
    }
}