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
        println("WeatherViewModel: Fetching weather for Latitude=$lat, Longitude=$lon")
        val internetAvailable = isInternetAvailable(context)
        _isConnected.postValue(internetAvailable)

        if (internetAvailable) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val response = weatherService.getWeatherForecast(lat, lon).execute()
                    println("API Request Sent")
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()
                        println("API Response: $body")
                        if (body != null && body.daily.time != null && body.daily.temperature_2m_max != null) {
                            val weatherList = body.daily.time.mapIndexed { index, date ->
                                WeatherData(
                                    date = date,
                                    temperature = body.daily.temperature_2m_max.getOrNull(index) ?: 0.0,
                                    cloudCoverage = "Unknown" // Placeholder
                                )
                            }
                            _weatherData.postValue(weatherList)
                            cacheWeatherData(weatherList)
                            println("Weather data updated: $weatherList")
                        } else {
                            println("Invalid API Response: Missing fields")
                            _weatherData.postValue(emptyList())
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
            val cachedData = getCachedWeatherData()
            if (cachedData != null) {
                println("Using cached data: $cachedData")
                _weatherData.postValue(cachedData)
            } else {
                println("No internet and no cached data found.")
                _weatherData.postValue(emptyList()) // Provide an empty list as a fallback
            }

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

