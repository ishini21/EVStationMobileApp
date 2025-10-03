package com.example.evstationmobileapp

import com.example.evstationmobileapp.R
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var navHome: LinearLayout? = null
    private var navStations: LinearLayout? = null
    private var navBookings: LinearLayout? = null
    private var navProfile: LinearLayout? = null

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        // Initialize navigation items
        navHome = findViewById(R.id.navHome)
        navStations = findViewById(R.id.navStations)
        navBookings = findViewById(R.id.navBookings)
        navProfile = findViewById(R.id.navProfile)

        // Set click listeners
        navHome!!.setOnClickListener(View.OnClickListener { v: View? -> })

        navStations!!.setOnClickListener(View.OnClickListener { v: View? -> })

        navBookings!!.setOnClickListener(View.OnClickListener { v: View? -> })

        navProfile!!.setOnClickListener(View.OnClickListener { v: View? -> })
    }
}