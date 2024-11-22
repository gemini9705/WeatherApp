package com.example.weatherapp.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.model.WeatherData
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                // Observe weather data from ViewModel
                val weatherList by weatherViewModel.weatherData.observeAsState(emptyList())

                // Scaffold with updated layout
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        // Input fields for latitude and longitude
                        WeatherInput(
                            onFetchWeather = { latitude, longitude ->
                                weatherViewModel.fetchWeather(latitude, longitude)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .weight(1f) // Fix height for input section
                        )

                        WeatherList(
                            weatherData = weatherList,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(3f) // Allocate remaining space to the list
                        )
                    }
                }
            }
        }
    }
}
