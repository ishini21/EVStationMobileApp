package com.example.evstationmobileapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.evstationmobileapp.R
import com.example.evstationmobileapp.models.Booking
import com.example.evstationmobileapp.models.BookingStatus

class BookingAdapter(
    private val bookings: List<Booking>,
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
        val btnViewQR: Button = itemView.findViewById(R.id.btnViewQR)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking_card, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val booking = bookings[position]

        holder.tvStationName.text = booking.stationName
        holder.tvLocation.text = booking.location
        holder.tvDate.text = booking.date
        holder.tvTime.text = booking.time

        // Set status text and background
        when (booking.status) {
            BookingStatus.PENDING -> {
                holder.tvStatus.text = "Pending"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                holder.layoutButtons.visibility = View.VISIBLE
            }
            BookingStatus.COMPLETED -> {
                holder.tvStatus.text = "Completed"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_completed)
                holder.layoutButtons.visibility = View.GONE
            }
            BookingStatus.ONGOING -> {
                holder.tvStatus.text = "Ongoing"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_ongoing)
                holder.layoutButtons.visibility = View.VISIBLE
            }
            BookingStatus.CANCELED -> {
                holder.tvStatus.text = "Canceled"
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_canceled)
                holder.layoutButtons.visibility = View.GONE
            }
        }

        // Set button click listeners
        holder.btnViewQR.setOnClickListener {
            onViewQRClick(booking)
        }

        holder.btnCancel.setOnClickListener {
            onCancelClick(booking)
        }
    }

    override fun getItemCount(): Int = bookings.size
}