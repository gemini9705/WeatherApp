package com.example.weatherapp.model

data class HourlyWeatherApiResponse(
    val hourly: HourlyWeather
)

data class HourlyWeather(
    val time: List<String>, // List of timestamps
    val temperature_2m: List<Double>, // List of hourly temperatures
    val cloud_cover: List<Double>, // List of hourly cloud cover percentages
    val precipitation: List<Double>?, // List of hourly precipitation
    val snowfall: List<Double>? // List of hourly snowfall
)
