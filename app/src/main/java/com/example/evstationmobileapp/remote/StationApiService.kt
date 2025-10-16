package com.example.evstationmobileapp.remote

import com.example.evstationmobileapp.models.Station
import com.example.evstationmobileapp.models.UpdateSlotAvailabilityDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object StationApiService {

    private const val BASE_URL = "http://10.0.2.2:5276/api/Stations"
    private const val SLOTS_BASE_URL = "http://10.0.2.2:5276/api/Slots"

    /**
     * Get station by ID with full details including operators and slots
     */
    suspend fun getStationById(stationId: String): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$BASE_URL/$stationId")
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")

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
                        put("error", "Station Fetch Failed")
                        put("details", errorResponse)
                        put("statusCode", responseCode)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Network Error")
                    put("message", e.message ?: "Unknown network error")
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }

    /**
     * Update slot availability status
     */
    suspend fun updateSlotAvailability(
        slotId: String, 
        updateDto: UpdateSlotAvailabilityDto
    ): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$SLOTS_BASE_URL/$slotId/availability")
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "PATCH"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
                connection.doOutput = true

                val jsonRequestBody = JSONObject().apply {
                    put("slotStatus", updateDto.slotStatus.name)
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
                        put("error", "Slot Update Failed")
                        put("details", errorResponse)
                        put("statusCode", responseCode)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Network Error")
                    put("message", e.message ?: "Unknown network error")
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }

    /**
     * Update slot availability status (convenience method with string status)
     */
    suspend fun updateSlotAvailability(
        slotId: String, 
        status: String
    ): String {
        val updateDto = UpdateSlotAvailabilityDto(
            slotStatus = when (status.lowercase()) {
                "available" -> com.example.evstationmobileapp.models.SlotStatus.Available
                "occupied" -> com.example.evstationmobileapp.models.SlotStatus.Occupied
                "maintenance" -> com.example.evstationmobileapp.models.SlotStatus.Maintenance
                else -> com.example.evstationmobileapp.models.SlotStatus.Available
            }
        )
        return updateSlotAvailability(slotId, updateDto)
    }

    /**
     * Get all active station operators
     */
    suspend fun getStationActiveOperators(): String {
        return withContext(Dispatchers.IO) {
            val url = URL("$BASE_URL/operators")
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")

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
                        put("error", "Operators Fetch Failed")
                        put("details", errorResponse)
                        put("statusCode", responseCode)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Network Error")
                    put("message", e.message ?: "Unknown network error")
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }

    /**
     * Get all stations (if needed for future use)
     */
    suspend fun getAllStations(): String {
        return withContext(Dispatchers.IO) {
            val url = URL(BASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            var result = ""

            try {
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")

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
                        put("error", "Stations Fetch Failed")
                        put("details", errorResponse)
                        put("statusCode", responseCode)
                    }.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                result = JSONObject().apply {
                    put("error", "Network Error")
                    put("message", e.message ?: "Unknown network error")
                }.toString()
            } finally {
                connection.disconnect()
            }
            result
        }
    }
}
