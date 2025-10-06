package com.example.evstationmobileapp.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.databinding.ActivityProfileBinding
import com.example.evstationmobileapp.db.UserDbHelper
import com.example.evstationmobileapp.models.EVOwner

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var dbHelper: UserDbHelper
    private var currentUser: EVOwner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = UserDbHelper(this)

        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val userNIC = sharedPref.getString("user_nic", "") ?: ""

        currentUser = dbHelper.getUserByNIC(userNIC)

        currentUser?.let { user ->
            binding.etNIC.setText(user.nic)
            binding.etFirstName.setText(user.firstName)
            binding.etLastName.setText(user.lastName)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phone)

            // NIC cannot be changed
            binding.etNIC.isEnabled = false

            // Show status
            binding.tvStatus.text = if (user.isActive) "Active" else "Deactivated"
            binding.tvStatus.setBackgroundColor(
                if (user.isActive) getColor(android.R.color.holo_green_light)
                else getColor(android.R.color.holo_red_light)
            )
        }
    }

    private fun setupClickListeners() {
        binding.btnUpdate.setOnClickListener {
            if (validateForm()) {
                updateProfile()
            }
        }

        binding.btnDeactivate.setOnClickListener {
            showDeactivationConfirmation()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun validateForm(): Boolean {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (firstName.isEmpty()) {
            binding.etFirstName.error = "First name is required"
            return false
        }

        if (lastName.isEmpty()) {
            binding.etLastName.error = "Last name is required"
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone number is required"
            return false
        }

        // Basic email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            return false
        }

        return true
    }

    private fun updateProfile() {
        currentUser?.let { user ->
            val updatedUser = EVOwner(
                nic = user.nic,
                firstName = binding.etFirstName.text.toString().trim(),
                lastName = binding.etLastName.text.toString().trim(),
                email = binding.etEmail.text.toString().trim(),
                phone = binding.etPhone.text.toString().trim(),
                password = user.password, // Keep existing password
                isActive = user.isActive
            )

            if (dbHelper.updateUser(updatedUser)) {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Update session if email changed
                if (user.email != updatedUser.email) {
                    updateUserSession(updatedUser)
                }
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showDeactivationConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Deactivate Account")
            .setMessage("Are you sure you want to deactivate your account? You will not be able to log in until a back-office officer reactivates your account.")
            .setPositiveButton("Deactivate") { _: DialogInterface, _: Int ->
                deactivateAccount()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deactivateAccount() {
        currentUser?.let { user ->
            if (dbHelper.deactivateUser(user.nic)) {
                Toast.makeText(this, "Account deactivated successfully", Toast.LENGTH_LONG).show()

                // Logout user
                logoutUser()
            } else {
                Toast.makeText(this, "Failed to deactivate account", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateUserSession(user: EVOwner) {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_email", user.email)
            putString("user_name", "${user.firstName} ${user.lastName}")
            apply()
        }
    }

    private fun logoutUser() {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}