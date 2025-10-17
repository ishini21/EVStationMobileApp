package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evstationmobileapp.MainActivity
import com.example.evstationmobileapp.databinding.ActivityProfileBinding
import com.example.evstationmobileapp.db.UserDbHelper
import com.example.evstationmobileapp.models.EVOwner
import com.example.evstationmobileapp.remote.EVOwnerApiService
import com.example.evstationmobileapp.utils.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    companion object {
        private const val UPDATE_PROFILE_REQUEST_CODE = 1001
    }

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

        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        // Handle back button click
        binding.btnBack.setOnClickListener {
            // Navigate back to MainActivity (dashboard)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun setupClickListeners() {
        binding.btnUpdate.setOnClickListener {
            try {
                val intent = Intent(this, UpdateProfileActivity::class.java)
                startActivityForResult(intent, UPDATE_PROFILE_REQUEST_CODE)
            } catch (e: Exception) {
                Toast.makeText(this, "Error opening update page: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        binding.btnDeactivate.setOnClickListener {
            showDeactivateConfirmation()
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
                binding.tvNic.text = user.nic
                binding.tvFirstName.text = user.firstName
                binding.tvLastName.text = user.lastName
                binding.tvEmail.text = user.email
                binding.tvPhone.text = user.phone

                // Show EV Owner specific buttons
                binding.btnUpdate.visibility = View.VISIBLE
                binding.btnDeactivate.visibility = View.VISIBLE
            }
        } else {
            // No user found locally, so this must be a Station Operator.
            // The 'userIdentifier' is their email.
            val firstName = sessionManager.fetchUserfirstName()

            binding.tvFirstName.text = firstName ?: "Station Operator"
            binding.tvLastName.text = ""
            binding.tvNic.text = "N/A"
            binding.tvEmail.text = userIdentifier // Display the email
            binding.tvPhone.text = "N/A"

            // Hide buttons not applicable to Station Operators
            binding.btnUpdate.visibility = View.GONE
            binding.btnDeactivate.visibility = View.GONE
        }
    }

    private fun showDeactivateConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Deactivate Account")
            .setMessage("Are you sure you want to deactivate your account? This action can only be reversed by a back-office officer.")
            .setPositiveButton("Deactivate") { _, _ -> deactivateAccount() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ -> logoutUser() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deactivateAccount() {
        val userNic = currentUser?.nic
        if (userNic.isNullOrEmpty()) {
            Toast.makeText(this, "User information not found", Toast.LENGTH_LONG).show()
            return
        }

        lifecycleScope.launch {
            try {
                val authToken = sessionManager.fetchAuthToken()
                if (authToken.isNullOrEmpty()) {
                    Toast.makeText(this@ProfileActivity, "No authentication token. Please log in again.", Toast.LENGTH_LONG).show()
                    return@launch
                }

                val resultJsonString = EVOwnerApiService.deactivateAccount(userNic, authToken)
                val resultJson = JSONObject(resultJsonString)

                if (resultJson.has("success") && resultJson.getBoolean("success")) {
                    // Account deactivated successfully
                    Toast.makeText(this@ProfileActivity, "Account deactivated successfully", Toast.LENGTH_SHORT).show()
                    
                    // Update local database to mark account as inactive
                    currentUser?.let { user ->
                        val deactivatedUser = user.copy(isActive = false)
                        dbHelper.updateUser(deactivatedUser)
                    }
                    
                    // Logout user and redirect to login
                    logoutUser()
                } else {
                    val errorMessage = resultJson.optString("details", resultJson.optString("error", "Deactivation failed"))
                    Toast.makeText(this@ProfileActivity, "Deactivation failed: $errorMessage", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Network error. Please check your connection and try again.", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun logoutUser() {
        sessionManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == UPDATE_PROFILE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Profile was updated successfully, reload the data
                loadUserData()
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}