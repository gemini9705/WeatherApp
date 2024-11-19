package com.example.weatherapp.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weatherapp.model.WeatherData

@Composable
fun WeatherList(weatherData: List<WeatherData>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(weatherData) { weather ->
            WeatherItem(weather)
        }
    }
}
