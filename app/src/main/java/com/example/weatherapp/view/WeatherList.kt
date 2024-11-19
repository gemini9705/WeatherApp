package com.example.weatherapp.view

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.weatherapp.model.WeatherData

@Composable
fun WeatherList(
    weatherData: List<WeatherData>,
    modifier: Modifier = Modifier // Add the modifier parameter with a default value
) {
    LazyColumn(modifier = modifier) { // Use the passed modifier
        items(weatherData) { weather ->
            WeatherItem(weather)
        }
    }
}

