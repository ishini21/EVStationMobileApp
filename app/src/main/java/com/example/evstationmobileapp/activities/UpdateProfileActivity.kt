package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evstationmobileapp.databinding.ActivityUpdateProfileBinding
import com.example.evstationmobileapp.db.UserDbHelper
import com.example.evstationmobileapp.models.EVOwner
import com.example.evstationmobileapp.models.UpdateEVOwnerDto
import com.example.evstationmobileapp.remote.EVOwnerApiService
import com.example.evstationmobileapp.utils.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class UpdateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateProfileBinding
    private lateinit var dbHelper: UserDbHelper
    private lateinit var sessionManager: SessionManager
    private var currentUser: EVOwner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        println("UpdateProfileActivity: onCreate called")
        
        dbHelper = UserDbHelper(this)
        sessionManager = SessionManager(this)

        setupToolbar()
        loadUserData()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Update Profile"
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                updateProfile()
            }
        }

        binding.btnCancel.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loadUserData() {
        println("UpdateProfileActivity: loadUserData called")
        
        if (!sessionManager.isLoggedIn()) {
            println("UpdateProfileActivity: User not logged in")
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val userIdentifier = sessionManager.fetchUserIdentifier()
        println("UpdateProfileActivity: User identifier = $userIdentifier")
        
        if (userIdentifier == null) {
            println("UpdateProfileActivity: User identifier is null")
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Load user data from local database
        val localUser = dbHelper.getUserByNIC(userIdentifier)
        if (localUser != null) {
            println("UpdateProfileActivity: Found user in local DB: $localUser")
            currentUser = localUser
            populateFields(localUser)
        } else {
            println("UpdateProfileActivity: No user found in local DB")
            Toast.makeText(this, "User data not found. Please log in again.", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun populateFields(user: EVOwner) {
        println("UpdateProfileActivity: Populating fields with user data")
        println("UpdateProfileActivity: NIC: ${user.nic}, FirstName: ${user.firstName}, LastName: ${user.lastName}, Email: ${user.email}, Phone: ${user.phone}")
        
        binding.etNic.setText(user.nic)
        binding.etFirstName.setText(user.firstName)
        binding.etLastName.setText(user.lastName)
        binding.etEmail.setText(user.email)
        binding.etPhone.setText(user.phone)
        
        println("UpdateProfileActivity: Fields populated successfully")
    }

    private fun validateForm(): Boolean {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        var isValid = true

        if (firstName.isEmpty()) {
            binding.etFirstName.error = "First name is required"
            isValid = false
        }

        if (lastName.isEmpty()) {
            binding.etLastName.error = "Last name is required"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            isValid = false
        } else if (!isValidEmail(email)) {
            binding.etEmail.error = "Please enter a valid email"
            isValid = false
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone number is required"
            isValid = false
        } else if (!isValidPhone(phone)) {
            binding.etPhone.error = "Please enter a valid phone number"
            isValid = false
        }

        return isValid
    }

    private fun updateProfile() {
        val nic = binding.etNic.text.toString().trim()
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()

        if (nic.isEmpty()) {
            Toast.makeText(this, "NIC is required for update", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                val authToken = sessionManager.fetchAuthToken()
                println("UpdateProfileActivity: Updating profile for NIC: $nic")
                println("UpdateProfileActivity: Auth token = ${authToken?.take(10)}...")
                
                if (authToken.isNullOrEmpty()) {
                    Toast.makeText(this@UpdateProfileActivity, "No authentication token. Please log in again.", Toast.LENGTH_LONG).show()
                    finish()
                    return@launch
                }
                
                // Create DTO for API call
                val updateDto = UpdateEVOwnerDto(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phone = phone,
                    password = null // No password update for now
                )
                
                val resultJsonString = EVOwnerApiService.updateEVOwnerProfile(
                    nic, updateDto, authToken
                )
                println("UpdateProfileActivity: Update response = $resultJsonString")
                val resultJson = JSONObject(resultJsonString)

                if (!resultJson.has("error")) {
                    // Update successful on backend, now update local database
                    val updatedUser = currentUser?.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone
                    )

                    if (updatedUser != null && dbHelper.updateUser(updatedUser)) {
                        currentUser = updatedUser
                        Toast.makeText(this@UpdateProfileActivity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        
                        // Return to profile page with success
                        val resultIntent = Intent()
                        resultIntent.putExtra("profile_updated", true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this@UpdateProfileActivity, "Update successful on server but failed to save locally.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorMessage = resultJson.optString("details", resultJson.optString("error", "Update failed"))
                    println("UpdateProfileActivity: Update error = $errorMessage")
                    
                    // Try local update as fallback
                    val updatedUser = currentUser?.copy(
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone
                    )
                    
                    if (updatedUser != null && dbHelper.updateUser(updatedUser)) {
                        currentUser = updatedUser
                        Toast.makeText(this@UpdateProfileActivity, "Profile updated locally! (Backend sync pending)", Toast.LENGTH_SHORT).show()
                        
                        // Return to profile page with success
                        val resultIntent = Intent()
                        resultIntent.putExtra("profile_updated", true)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    } else {
                        Toast.makeText(this@UpdateProfileActivity, "Update failed: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                println("UpdateProfileActivity: Exception in updateProfile = ${e.message}")
                // Try local update as fallback
                val updatedUser = currentUser?.copy(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phone = phone
                )
                
                if (updatedUser != null && dbHelper.updateUser(updatedUser)) {
                    currentUser = updatedUser
                    Toast.makeText(this@UpdateProfileActivity, "Profile updated locally! (Backend sync pending)", Toast.LENGTH_SHORT).show()
                    
                    // Return to profile page with success
                    val resultIntent = Intent()
                    resultIntent.putExtra("profile_updated", true)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this@UpdateProfileActivity, "Update failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnSave.isEnabled = !isLoading
        binding.btnCancel.isEnabled = !isLoading
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = android.util.Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = java.util.regex.Pattern.compile("^[0-9+\\-\\s()]{10,}\$")
        return phonePattern.matcher(phone).matches()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
