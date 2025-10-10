package com.example.evstationmobileapp.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context, // The context is now the first parameter
        "secure_user_session",
        masterKey, // Pass the key object we just created
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String, identifier: String, firstName: String , userId: String) {
        with(sharedPreferences.edit()) {
            putString("auth_token", token)
            putString("user_identifier", identifier)
            putString("first_name", firstName )
            putString("user_id", userId)// Save the full name
            apply()
        }
    }

    fun fetchUserfirstName(): String? {
        return sharedPreferences.getString("first_name", null)
    }
    fun fetchUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }
    fun fetchAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
    fun fetchUserIdentifier(): String? {
        return sharedPreferences.getString("user_identifier", null)
    }

    fun isLoggedIn(): Boolean {
        return !fetchAuthToken().isNullOrEmpty()
    }

    fun logout() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
            print("came here")
        }
    }
}