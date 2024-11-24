package com.example.weatherapp.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingService {
    @GET("search")
    suspend fun getCoordinates(@Query("q") location: String): List<GeocodeResponse>

    @GET("reverse")
    fun getLocationName(
        @Query("lat") lat: Float,
        @Query("lon") lon: Float,
        @Query("format") format: String = "json"
    ): Call<ReverseGeocodeResponse>
}

data class GeocodeResponse(
    val lat: String,
    val lon: String
)

data class ReverseGeocodeResponse(
    val name: String?, // Simplify to get the location name
    val address: Address?
)

data class Address(
    val city: String?,
    val state: String?,
    val country: String?
)
