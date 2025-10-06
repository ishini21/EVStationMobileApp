package com.example.evstationmobileapp.activities
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.databinding.ActivityRegistrationBinding
import com.example.evstationmobileapp.db.UserDbHelper
import com.example.evstationmobileapp.models.EVOwner
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var dbHelper: UserDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = UserDbHelper(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnRegister.setOnClickListener {
            if (validateForm()) {
                registerUser()
            }
        }

        binding.tvLogin.setOnClickListener {
            finish() // Go back to login
        }
    }

    private fun validateForm(): Boolean {
        val nic = binding.etNIC.text.toString().trim()
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // NIC validation (Sri Lankan NIC format)
        if (nic.isEmpty()) {
            binding.etNIC.error = "NIC is required"
            return false
        }

        // Name validation
        if (firstName.isEmpty()) {
            binding.etFirstName.error = "First name is required"
            return false
        }

        if (lastName.isEmpty()) {
            binding.etLastName.error = "Last name is required"
            return false
        }

        // Email validation
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }

        if (!isValidEmail(email)) {
            binding.etEmail.error = "Please enter a valid email"
            return false
        }

        // Phone validation
        if (phone.isEmpty()) {
            binding.etPhone.error = "Phone number is required"
            return false
        }

        if (!isValidPhone(phone)) {
            binding.etPhone.error = "Please enter a valid phone number"
            return false
        }

        // Password validation
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return false
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun registerUser() {
        val nic = binding.etNIC.text.toString().trim()
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val password = binding.etPassword.text.toString()

        // Check if user already exists with same NIC
        if (dbHelper.getUserByNIC(nic) != null) {
            Toast.makeText(this, "User with this NIC already exists", Toast.LENGTH_LONG).show()
            return
        }

        // Check if email already exists
        if (dbHelper.isEmailExists(email)) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_LONG).show()
            return
        }

        val newUser = EVOwner(
            nic = nic,
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            password = password
        )

        if (dbHelper.createUser(newUser)) {
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
            finish() // Go back to login
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
        )
        return emailPattern.matcher(email).matches()
    }

    private fun isValidPhone(phone: String): Boolean {
        val phonePattern = Pattern.compile("^[0-9+\\-\\s()]{10,}\$")
        return phonePattern.matcher(phone).matches()
    }
}