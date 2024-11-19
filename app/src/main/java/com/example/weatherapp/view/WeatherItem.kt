package com.example.weatherapp.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherData

@Composable
fun WeatherItem(weather: WeatherData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Weather Icon
        Image(
            painter = painterResource(
                id = when (weather.cloudCoverage) {
                    "Cloudy" -> R.drawable.ic_cloudy
                    "Partly Cloudy" -> R.drawable.ic_partlycloudy
                    "Sunny" -> R.drawable.ic_sunny
                    "Rainy" -> R.drawable.ic_rain
                    else -> R.drawable.ic_default
                }
            ),
            contentDescription = "Weather icon",
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Weather Details
        Column {
            Text(text = "Date: ${weather.date}")
            Text(text = "Temperature: ${weather.temperature}°C")
            Text(text = "Condition: ${weather.cloudCoverage}")
        }
    }
}
