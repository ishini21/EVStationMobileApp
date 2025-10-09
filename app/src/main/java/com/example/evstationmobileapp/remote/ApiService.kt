package com.example.evstationmobileapp.remote

import com.example.evstationmobileapp.models.BookingRequest
import com.example.evstationmobileapp.models.BookingResponse
import com.example.evstationmobileapp.models.ChargingStation
import com.example.evstationmobileapp.models.Slot
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/api/Stations")
    suspend fun getStations(): Response<List<ChargingStation>>

    @GET("/api/Stations/{id}")
    suspend fun getStationById(@Path("id") stationId: String): Response<ChargingStation>

    @GET("/api/Bookings/available-slots")
    suspend fun getAvailableSlots(
        @Header("Authorization") token: String,
        @Query("stationId") stationId: String,
        @Query("startTime") startTime: String, // ISO 8601 UTC format
        @Query("endTime") endTime: String      // ISO 8601 UTC format
    ): Response<List<Slot>>

    @POST("/api/Bookings")
    suspend fun createBooking(
        @Header("Authorization") token: String,
        @Body bookingRequest: BookingRequest
    ): Response<ResponseBody>

    @GET("/api/Bookings")
    suspend fun getBookingsByNic(
        @Header("Authorization") token: String,
        @Query("CustomerNic") customerNic: String
    ): Response<BookingResponse>

    @POST("/api/Bookings/{id}/cancel")
    suspend fun cancelBooking(
        @Header("Authorization") token: String,
        @Path("id") bookingId: String
    ): Response<ResponseBody>

}