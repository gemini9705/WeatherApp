package com.example.weatherapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel
import com.example.weatherapp.model.WeatherData

class MainActivity : ComponentActivity() {
    // Reference the ViewModel
    private val weatherViewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                // Observe the ViewModel's LiveData
                val weatherList by weatherViewModel.weatherData.observeAsState(emptyList())

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherList(
                        weatherData = weatherList,
                        modifier = Modifier.padding(innerPadding)
                    )
                }

                // Fetch data for example coordinates
                weatherViewModel.fetchWeather(59.3293f, 18.0686f) // Example: Stockholm
            }
        }
    }
}

@Composable
fun WeatherList(weatherData: List<WeatherData>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(weatherData) { weather ->
            WeatherItem(weather)
        }
    }
}

@Composable
fun WeatherItem(weather: WeatherData) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Date: ${weather.date}", style = androidx.compose.ui.text.TextStyle.Default)
        Text(text = "Temperature: ${weather.temperature}°C")
        Text(text = "Condition: ${weather.cloudCoverage}")
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherListPreview() {
    val sampleWeather = listOf(
        WeatherData("2024-11-18", 10.0, "Cloudy"),
        WeatherData("2024-11-19", 12.5, "Sunny"),
        WeatherData("2024-11-20", 8.0, "Rainy")
    )
    WeatherAppTheme {
        WeatherList(weatherData = sampleWeather)
    }
}
