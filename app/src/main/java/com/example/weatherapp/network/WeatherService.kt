package com.example.weatherapp.network

import com.example.weatherapp.model.HourlyWeatherApiResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast")
    fun getWeatherForecast(
        @Query("latitude") lat: Float,
        @Query("longitude") lon: Float,
        @Query("hourly") hourly: String = "temperature_2m,cloud_cover,precipitation",
        @Query("timezone") timezone: String = "auto"
    ): Call<HourlyWeatherApiResponse>
}
