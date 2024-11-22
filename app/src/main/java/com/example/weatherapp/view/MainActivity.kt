package com.example.weatherapp.view

import android.os.Bundle
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
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel
import androidx.compose.material3.TopAppBar

class MainActivity : ComponentActivity() {
    private val weatherViewModel: WeatherViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory(application)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme {
                val weatherList by weatherViewModel.weatherData.observeAsState(emptyList())
                val isConnected by weatherViewModel.isConnected.observeAsState(true)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = "Weather Forecast")
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(innerPadding)) {
                        if (!isConnected) {
                            Text(
                                text = "No internet connection. Showing cached data.",
                                color = androidx.compose.ui.graphics.Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }

                        WeatherInput(
                            onFetchWeather = { latitude, longitude ->
                                weatherViewModel.fetchWeather(latitude, longitude)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .weight(1f)
                        )

                        WeatherList(
                            weatherData = weatherList,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(3f)
                        )
                    }
                }
            }
        }
    }
}


