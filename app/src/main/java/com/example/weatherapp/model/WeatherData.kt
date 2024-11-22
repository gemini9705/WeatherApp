package com.example.weatherapp.model

data class WeatherData(
    val date: String,
    val temperature: Double,
    val cloudCoverage: String // "Cloudy", "Sunny"
)
