package com.example.weatherapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.WeatherService
import com.example.weatherapp.utils.isInternetAvailable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel(application: Application) : AndroidViewModel(application)  {
    private val context = application.applicationContext
    private val _weatherData = MutableLiveData<List<WeatherData>>(emptyList())
    val weatherData: LiveData<List<WeatherData>> get() = _weatherData

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService = retrofit.create(WeatherService::class.java)

    fun fetchWeather(lat: Float, lon: Float) {
        val internetAvailable = isInternetAvailable(context)
        _isConnected.postValue(internetAvailable)

        if (internetAvailable) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = weatherService.getWeatherForecast(lat, lon).execute()
                    if (response.isSuccessful && response.body() != null) {
                        println("API Response: ${response.body()?.hourly}")
                        val body = response.body()
                        if (body != null && body.hourly.time != null && body.hourly.temperature_2m != null) {
                            val weatherList = body.hourly.time.mapIndexed { index, timestamp ->
                                val cloudCover = body.hourly.cloud_cover?.getOrNull(index) ?: 0.0
                                val precipitation = body.hourly.precipitation?.getOrNull(index) ?: 0.0
                                val snowfall = body.hourly.snowfall?.getOrNull(index) ?: 0.0


                                val condition = when {
                                    snowfall > 0 -> "Snow" // Snow if snowfall > 0
                                    precipitation > 0 -> "Rain" // Rain if precipitation > 0
                                    cloudCover < 30 -> "Sunny" // Low cloud cover
                                    cloudCover in 30.0..70.0 -> "Partly Cloudy" // Moderate cloud cover
                                    cloudCover > 70 -> "Cloudy" // High cloud cover
                                    else -> "Unknown"
                                }

                                WeatherData(
                                    date = timestamp, // Use hourly timestamp
                                    temperature = body.hourly.temperature_2m[index],
                                    cloudCoverage = condition
                                )
                            }
                            _weatherData.postValue(weatherList)
                            cacheWeatherData(weatherList)
                        }
                    } else {
                        println("API Error: ${response.errorBody()?.string()}")
                        _weatherData.postValue(emptyList())
                    }
                } catch (e: Exception) {
                    println("Error fetching weather: ${e.message}")
                    _weatherData.postValue(emptyList())
                }
            }
        } else {
            val cachedData = getCachedWeatherData() ?: emptyList()
            _weatherData.postValue(cachedData)
        }
    }

    private fun cacheWeatherData(weatherData: List<WeatherData>) {
        val json = Gson().toJson(weatherData)
        sharedPreferences.edit().putString("cached_weather_data", json).apply()
    }

    private fun getCachedWeatherData(): List<WeatherData>? {
        val json = sharedPreferences.getString("cached_weather_data", null)
        return if (json != null) {
            val type = object : TypeToken<List<WeatherData>>() {}.type
            Gson().fromJson(json, type)
        } else {
            null
        }
    }
}

