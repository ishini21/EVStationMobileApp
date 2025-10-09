package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.databinding.ActivityProfileBinding
import com.example.evstationmobileapp.db.UserDbHelper
import com.example.evstationmobileapp.models.EVOwner
import com.example.evstationmobileapp.utils.SessionManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var dbHelper: UserDbHelper
    private lateinit var sessionManager: SessionManager
    private var currentUser: EVOwner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = UserDbHelper(this)
        sessionManager = SessionManager(this)

        loadUserData()
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnUpdate.setOnClickListener {
            Toast.makeText(this, "Update feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnDeactivate.setOnClickListener {
            Toast.makeText(this, "Deactivation feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun loadUserData() {
        val userIdentifier = sessionManager.fetchUserIdentifier()

        if (userIdentifier == null) {
            // If there's no identifier at all, the session is invalid.
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            logoutUser()
            return
        }

        // --- THIS IS THE CORRECTED LOGIC ---
        // Try to find a user in the local DB with the identifier (which would be a NIC).
        currentUser = dbHelper.getUserByNIC(userIdentifier)

        if (currentUser != null) {
            // User was found locally, so they are an EV Owner.
            currentUser?.let { user ->
                binding.etNic.setText(user.nic)
                binding.etFirstName.setText(user.firstName)
                binding.etLastName.setText(user.lastName)
                binding.etEmail.setText(user.email)
                binding.etPhone.setText(user.phone)

                // Show EV Owner specific buttons
                binding.btnUpdate.visibility = View.VISIBLE
                binding.btnDeactivate.visibility = View.VISIBLE
            }
        } else {
            // No user found locally, so this must be a Station Operator.
            // The 'userIdentifier' is their email.
            val firstName = sessionManager.fetchUserfirstName()

            binding.etFirstName.setText(firstName ?: "Station Operator")
            binding.etLastName.setText("")
            binding.etNic.setText("N/A")
            binding.etEmail.setText(userIdentifier) // Display the email
            binding.etPhone.setText("N/A")

            // Disable fields not applicable to Station Operators
            binding.etNic.isEnabled = false
            binding.etEmail.isEnabled = false
            binding.etPhone.isEnabled = false

            // Hide buttons not applicable to Station Operators
            binding.btnUpdate.visibility = View.GONE
            binding.btnDeactivate.visibility = View.GONE
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ -> logoutUser() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logoutUser() {
        sessionManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}