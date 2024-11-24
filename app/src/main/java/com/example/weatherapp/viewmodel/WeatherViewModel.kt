package com.example.weatherapp.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.GeocodingService
import com.example.weatherapp.network.WeatherService
import com.example.weatherapp.utils.isInternetAvailable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext

    // LiveData for weather data and connection status
    private val _weatherData = MutableLiveData<List<WeatherData>>(emptyList())
    val weatherData: LiveData<List<WeatherData>> get() = _weatherData

    private val _favorites = MutableLiveData<List<String>>(emptyList())
    val favorites: LiveData<List<String>> get() = _favorites

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String> get() = _locationName

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)


    private val retrofit = Retrofit.Builder() //retrofit library
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService = retrofit.create(WeatherService::class.java)
    private val geocodingRetrofit = Retrofit.Builder()
        .baseUrl("https://geocode.maps.co/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val geocodingService = geocodingRetrofit.create(GeocodingService::class.java)

    // Fetch weather and resolve location name
    fun fetchWeather(lat: Float, lon: Float) {
        val internetAvailable = isInternetAvailable(context)
        _isConnected.postValue(internetAvailable)

        if (internetAvailable) {
            resolveLocationName(lat, lon) // Resolve location name in the background
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
                                    snowfall > 0 -> "Snow"
                                    precipitation > 0 -> "Rain"
                                    cloudCover < 30 -> "Sunny"
                                    cloudCover in 30.0..70.0 -> "Partly Cloudy"
                                    cloudCover > 70 -> "Cloudy"
                                    else -> "Unknown"
                                }

                                WeatherData(
                                    date = timestamp,
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

    // Geocode to resolve location name
    private fun resolveLocationName(lat: Float, lon: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = geocodingService.getLocationName(lat, lon).execute()
                if (response.isSuccessful && response.body() != null) {
                    println("Geocoding API Response: ${response.body()}")
                    val locationName = response.body()?.address?.let { address ->
                        listOfNotNull(address.city, address.state, address.country).joinToString(", ")
                    } ?: "Unknown Location"
                    _locationName.postValue(locationName)
                } else {
                    println("Geocoding API Error: ${response.errorBody()?.string()}")
                    _locationName.postValue("Unknown Location")
                }
            } catch (e: Exception) {
                println("Error fetching location name: ${e.message}")
                _locationName.postValue("Unknown Location")
            }
        }
    }

    fun addFavorite(location: String) {
        val updatedFavorites = (_favorites.value ?: emptyList()) + location
        _favorites.postValue(updatedFavorites)
        saveFavorites(updatedFavorites)
    }

    fun removeFavorite(location: String) {
        val updatedFavorites = (_favorites.value ?: emptyList()).filter { it != location }
        _favorites.postValue(updatedFavorites)
        saveFavorites(updatedFavorites)
    }

    private fun saveFavorites(favorites: List<String>) {
        val json = Gson().toJson(favorites)
        sharedPreferences.edit().putString("favorites", json).apply()
    }

    private fun loadFavorites() {
        val json = sharedPreferences.getString("favorites", null)
        if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            val favorites = Gson().fromJson<List<String>>(json, type)
            _favorites.postValue(favorites)
        }
    }

    // Cache weather data
    private fun cacheWeatherData(weatherData: List<WeatherData>) {
        val json = Gson().toJson(weatherData)
        sharedPreferences.edit().putString("cached_weather_data", json).apply()
    }

    // Retrieve cached weather data
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
