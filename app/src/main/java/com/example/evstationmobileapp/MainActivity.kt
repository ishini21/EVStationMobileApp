package com.example.evstationmobileapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.activities.BookingsActivity
import com.example.evstationmobileapp.activities.ProfileActivity
import com.example.evstationmobileapp.activities.StationsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    // You only need one variable for the entire navigation view now
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // The old LinearLayout code is replaced with this single listener
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_owner_home -> {
                    // You are already on the home screen, so do nothing or refresh
                    true
                }
                R.id.nav_owner_stations -> {
                    startActivity(Intent(this, StationsActivity::class.java))
                    true
                }
                R.id.nav_owner_bookings -> {
                    startActivity(Intent(this, BookingsActivity::class.java))
                    true
                }
                R.id.nav_owner_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}