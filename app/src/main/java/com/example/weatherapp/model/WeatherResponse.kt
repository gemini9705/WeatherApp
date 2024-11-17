package com.example.weatherapp.model

data class WeatherResponse(
    val daily: DailyWeather
)

data class DailyWeather(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>
)
