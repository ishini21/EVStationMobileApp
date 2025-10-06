package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.evstationmobileapp.databinding.ActivityLoginBinding
import com.example.evstationmobileapp.db.UserDbHelper
import com.example.evstationmobileapp.models.EVOwner

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: UserDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = UserDbHelper(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            if (validateLoginForm()) {
                authenticateUser()
            }
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateLoginForm(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }

        return true
    }

    private fun authenticateUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        val user = dbHelper.authenticateUser(email, password)

        if (user != null) {
            // Save user session (you can use SharedPreferences)
            saveUserSession(user as EVOwner)

            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

            // Navigate to Dashboard
            val intent = Intent(this, StationDetailsActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserSession(user: com.example.evstationmobileapp.models.EVOwner) {
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_nic", user.nic)
            putString("user_email", user.email)
            putString("user_name", "${user.firstName} ${user.lastName}")
            putBoolean("is_logged_in", true)
            apply()
        }
    }
}