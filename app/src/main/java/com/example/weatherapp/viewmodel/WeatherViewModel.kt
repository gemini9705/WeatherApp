package com.example.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.network.WeatherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<List<WeatherData>>()
    val weatherData: LiveData<List<WeatherData>> get() = _weatherData

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherService = retrofit.create(WeatherService::class.java)

    fun fetchWeather(lat: Float, lon: Float) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = weatherService.getWeatherForecast(lat, lon).execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        val weatherList = it.daily.time.mapIndexed { index, date ->
                            WeatherData(
                                date = date,
                                temperature = it.daily.temperature_2m_max[index],
                                cloudCoverage = "Partly Cloudy" // Example value
                            )
                        }
                        _weatherData.postValue(weatherList)
                    }
                } else {
                    // Handle error
                }
            } catch (e: Exception) {
                // Handle exception
            }
        }
    }
}
