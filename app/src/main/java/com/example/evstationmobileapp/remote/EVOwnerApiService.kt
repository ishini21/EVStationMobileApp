package com.example.evstationmobileapp.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object EVOwnerApiService {

    private const val BASE_URL = "http://10.0.2.2:5276/api/EVOwners"
    private const val REGISTRATION_URL = "http://10.0.2.2:5276/api/EVOwners/self-register"
    private const val LOGIN_URL = "http://10.0.2.2:5276/api/auth/mobile-login"
    private const val UPDATE_URL = "http://10.0.2.2:5276/api/EVOwners/profile"
    private const val DEACTIVATE_URL = "http://10.0.2.2:5276/api/EVOwners/profile"


    suspend fun registerEVOwner(
        nic: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String
    ): String {
        return withContext(Dispatchers.IO) {
            val url = URL(REGISTRATION_URL)
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true

                val jsonRequestBody = JSONObject().apply {
                    put("nic", nic)
                    put("firstName", firstName)
                    put("lastName", lastName)
                    put("email", email)
                    put("phone", phone)
                    put("password", password)
                    put("isActive", true)
                }

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonRequestBody.toString())
                writer.flush()
                writer.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    result = reader.readText()
                    reader.close()
                } else {
                    val errorReader = BufferedReader(InputStreamReader(connection.errorStream))
                    val errorResponse = errorReader.readText()
                    errorReader.close()
                    result = JSONObject().apply {
                        put("error", "Registration Failed")
                        put("details", errorResponse)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Exception")
                    put("message", e.message)
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }


    suspend fun loginEVOwner(nic: String, password: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL(LOGIN_URL)
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true

                val jsonRequestBody = JSONObject().apply {
                    put("identifier", nic) // Use identifier field for existing auth endpoint
                    put("password", password)
                }

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonRequestBody.toString())
                writer.flush()
                writer.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    result = reader.readText()
                    reader.close()
                } else {
                    val errorReader = BufferedReader(InputStreamReader(connection.errorStream))
                    val errorResponse = errorReader.readText()
                    errorReader.close()
                    result = JSONObject().apply {
                        put("error", "Login Failed")
                        put("details", errorResponse)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Exception")
                    put("message", e.message)
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }

    /**
     * Update EV Owner profile
     */
    suspend fun updateEVOwner(
        nic: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String? = null,
        authToken: String? = null
    ): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$UPDATE_URL/$nic")
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "PUT"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                if (authToken != null) {
                    connection.setRequestProperty("Authorization", "Bearer $authToken")
                }
                connection.doOutput = true

                val jsonRequestBody = JSONObject().apply {
                    put("firstName", firstName)
                    put("lastName", lastName)
                    put("email", email)
                    put("phone", phone)
                    put("isActive", true)
                    if (!password.isNullOrEmpty()) {
                        put("password", password)
                    }
                }

                val writer = OutputStreamWriter(connection.outputStream)
                writer.write(jsonRequestBody.toString())
                writer.flush()
                writer.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    result = reader.readText()
                    reader.close()
                } else {
                    val errorReader = BufferedReader(InputStreamReader(connection.errorStream))
                    val errorResponse = errorReader.readText()
                    errorReader.close()
                    result = JSONObject().apply {
                        put("error", "Update Failed")
                        put("details", errorResponse)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Exception")
                    put("message", e.message)
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }

    /**
     * Deactivate EV Owner account
     */
    suspend fun deactivateEVOwner(nic: String, authToken: String? = null): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$DEACTIVATE_URL/$nic/deactivate")
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "PATCH"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                if (authToken != null) {
                    connection.setRequestProperty("Authorization", "Bearer $authToken")
                }
                connection.doOutput = true

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                    result = JSONObject().apply {
                        put("success", true)
                        put("message", "Account deactivated successfully")
                    }.toString()
                } else {
                    val errorReader = BufferedReader(InputStreamReader(connection.errorStream))
                    val errorResponse = errorReader.readText()
                    errorReader.close()
                    result = JSONObject().apply {
                        put("error", "Deactivation Failed")
                        put("details", errorResponse)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Exception")
                    put("message", e.message)
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }

    /**
     * Get EV Owner profile by NIC
     */
    suspend fun getEVOwnerProfile(nic: String, authToken: String? = null): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$BASE_URL/$nic")
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                if (authToken != null) {
                    connection.setRequestProperty("Authorization", "Bearer $authToken")
                }

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    result = reader.readText()
                    reader.close()
                } else {
                    val errorReader = BufferedReader(InputStreamReader(connection.errorStream))
                    val errorResponse = errorReader.readText()
                    errorReader.close()
                    result = JSONObject().apply {
                        put("error", "Fetch Failed")
                        put("details", errorResponse)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Network Error")
                    put("details", e.message ?: "Unknown network error")
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }
}
