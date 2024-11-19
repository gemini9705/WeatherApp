package com.example.weatherapp.model

data class WeatherApiResponse(
    val daily: DailyWeather
)

data class DailyWeather(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val cloudcover: List<Double>
)
