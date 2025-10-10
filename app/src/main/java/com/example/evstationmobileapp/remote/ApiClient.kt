package com.example.evstationmobileapp.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

//    private const val BASE_URL = "http://192.168.1.17:5276/api/"
    private const val BASE_URL = "http://10.0.2.2:5276/api/"


    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    // This is a suspend function to be called from a coroutine
    suspend fun loginUser(identifier: String, password: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL("${BASE_URL}auth/mobile-login")
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                // Configure the connection for a POST request
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true

                // Create the JSON request body
                val jsonRequestBody = JSONObject().apply {
                    put("identifier", identifier)
                    put("password", password)
                }

                // Write the body to the connection
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
                    result = errorResponse
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

    // You will add other functions like registerUser, updateUser, etc., here later.
}