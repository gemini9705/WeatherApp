package com.example.weatherapp.network

import com.example.weatherapp.model.WeatherApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    fun getWeatherForecast(
        @Query("latitude") lat: Float,
        @Query("longitude") lon: Float,
        @Query("daily") daily: String = "temperature_2m_max", // Ensure this matches API docs
        @Query("timezone") timezone: String = "auto"
    ): Call<WeatherApiResponse>
}
