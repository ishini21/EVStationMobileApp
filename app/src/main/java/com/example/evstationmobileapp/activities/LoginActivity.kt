package com.example.evstationmobileapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.evstationmobileapp.MainActivity
import com.example.evstationmobileapp.databinding.ActivityLoginBinding
import com.example.evstationmobileapp.db.UserDbHelper
import com.example.evstationmobileapp.models.EVOwner
import com.example.evstationmobileapp.remote.ApiClient
import com.example.evstationmobileapp.utils.SessionManager
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var dbHelper: UserDbHelper
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = UserDbHelper(this)
        sessionManager = SessionManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            if (validateLoginForm()) {
                performLogin()
            }
        }
        // ... your register listener
    }

    private fun performLogin() {
        showLoading(true)
        val identifier = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        lifecycleScope.launch {
            try {
                val resultJsonString = ApiClient.loginUser(identifier, password)
                val resultJson = JSONObject(resultJsonString)

                if (resultJson.has("token")) {
                    val token = resultJson.getString("token")
                    val role = resultJson.getString("role")
                    val firstName = resultJson.getString("firstName")
                    val lastName = resultJson.getString("lastName")
                    val userId = resultJson.getString("userId")

                    if (role == "EVOwner") {
                        // For EV Owners, get the NIC and save it as the identifier
                        val nic = resultJson.getString("nic")
                        sessionManager.saveAuthToken(token, nic, firstName , userId)

                        // Save the full user object to the local database
                        val user = EVOwner(
                            nic = nic,
                            firstName = firstName,
                            lastName = lastName,
                            email = resultJson.getString("email"),
                            phone = "", // Adjust if API provides this
                            password = "",
                            isActive = true
                        )
                        dbHelper.createUser(user)

                    } else if (role == "StationOperator") {
                        // For Station Operators, get their email and save it as the identifier
                        val email = resultJson.getString("email")
                        sessionManager.saveAuthToken(token, email, firstName , userId)
                        // Do NOT save the Station Operator to the local EVOwner database
                    }

                    navigateBasedOnRole(role)

                } else {
                    val errorMessage = resultJson.optString("message", "Invalid credentials.")
                    showError(errorMessage)
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Login API call failed with exception", e)
                showError("An error occurred. Please check your connection.")
                e.printStackTrace()
            } finally {
                showLoading(false)
            }
        }
    }
    private fun validateLoginForm(): Boolean {
        val identifier = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (identifier.isEmpty()) {
            binding.etEmail.error = "Email or NIC is required"
            binding.etEmail.requestFocus()
            return false
        }
        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateBasedOnRole(role: String) {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()

        val intent = when (role) {
            "EVOwner" -> {
                // If the user is an EVOwner, go to the main dashboard
                Intent(this, MainActivity::class.java)
            }
            "StationOperator" -> {
                // If the user is a StationOperator, go to their specific dashboard
                // NOTE: You will need to create this 'StationDashboardActivity'
                Intent(this, StationDashboardActivity::class.java)
            }
            else -> {
                // A fallback in case the role is something unexpected
                Intent(this, MainActivity::class.java)
            }
        }

        // Clear the back stack so the user can't go back to the login screen
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}