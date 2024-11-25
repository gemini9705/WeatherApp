package com.example.weatherapp.viewmodel

import com.example.weatherapp.repository.WeatherRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherData
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val weatherRepository = WeatherRepository(application.applicationContext)

    private val _weatherData = MutableLiveData<List<WeatherData>>(emptyList())
    val weatherData: LiveData<List<WeatherData>> get() = _weatherData

    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String> get() = _locationName

    private val _isConnected = MutableLiveData<Boolean>()
    val isConnected: LiveData<Boolean> get() = _isConnected

    private val _favorites = MutableLiveData<List<String>>(emptyList())
    val favorites: LiveData<List<String>> get() = _favorites

    fun fetchWeather(lat: Float, lon: Float) {
        viewModelScope.launch {
            val weather = weatherRepository.fetchWeather(lat, lon)
            _weatherData.postValue(weather)
        }
    }

    fun fetchLocationName(lat: Float, lon: Float) {
        viewModelScope.launch {
            val location = weatherRepository.fetchLocationName(lat, lon)
            _locationName.postValue(location)
        }
    }

    fun addFavorite(location: String) {
        val updatedFavorites = (_favorites.value ?: emptyList()) + location
        _favorites.postValue(updatedFavorites)
    }

    fun removeFavorite(location: String) {
        val updatedFavorites = (_favorites.value ?: emptyList()).filter { it != location }
        _favorites.postValue(updatedFavorites)
    }
}

