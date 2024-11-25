package com.example.weatherapp.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.GeocodingService
import com.example.weatherapp.network.WeatherService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService = retrofit.create(WeatherService::class.java)
    private val geocodingRetrofit = Retrofit.Builder()
        .baseUrl("https://geocode.maps.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geocodingService = geocodingRetrofit.create(GeocodingService::class.java)

    // Fetch weather data
    suspend fun fetchWeather(lat: Float, lon: Float): List<WeatherData> {
        return withContext(Dispatchers.IO) {
            try {
                val response = weatherService.getWeatherForecast(lat, lon).execute()
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()
                    body?.hourly?.let {
                        val weatherList = it.time.mapIndexed { index, timestamp ->
                            val cloudCover = it.cloud_cover?.getOrNull(index) ?: 0.0
                            val precipitation = it.precipitation?.getOrNull(index) ?: 0.0
                            val snowfall = it.snowfall?.getOrNull(index) ?: 0.0

                            val condition = when {
                                snowfall > 0 -> "Snow"
                                precipitation > 0 -> "Rain"
                                cloudCover < 30 -> "Sunny"
                                cloudCover in 30.0..70.0 -> "Partly Cloudy"
                                cloudCover > 70 -> "Cloudy"
                                else -> "Unknown"
                            }

                            WeatherData(
                                date = timestamp,
                                temperature = it.temperature_2m[index],
                                cloudCoverage = condition
                            )
                        }
                        cacheWeatherData(weatherList)
                        return@withContext weatherList
                    }
                }
                emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    // Fetch location name
    suspend fun fetchLocationName(lat: Float, lon: Float): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = geocodingService.getLocationName(lat, lon).execute()
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.address?.let { address ->
                        return@withContext listOfNotNull(
                            address.city, address.state, address.country
                        ).joinToString(", ")
                    }
                }
                "Unknown Location"
            } catch (e: Exception) {
                "Unknown Location"
            }
        }
    }

    // Cache weather data
    private fun cacheWeatherData(weatherData: List<WeatherData>) {
        val json = Gson().toJson(weatherData)
        sharedPreferences.edit().putString("cached_weather_data", json).apply()
    }

    // Retrieve cached weather data
    fun getCachedWeatherData(): List<WeatherData>? {
        val json = sharedPreferences.getString("cached_weather_data", null)
        return if (json != null) {
            val type = object : TypeToken<List<WeatherData>>() {}.type
            Gson().fromJson(json, type)
        } else {
            null
        }
    }
}
