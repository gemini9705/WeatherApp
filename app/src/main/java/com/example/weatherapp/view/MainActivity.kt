package com.example.weatherapp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.viewmodel.WeatherViewModel

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
                // Observing LiveData from ViewModel
                val weatherList by weatherViewModel.weatherData.observeAsState(emptyList())
                val isConnected by weatherViewModel.isConnected.observeAsState(true)
                val locationName by weatherViewModel.locationName.observeAsState("Unknown Location")
                val favoritesList by weatherViewModel.favorites.observeAsState(emptyList())

                // State to toggle between main and favorites views
                var showFavorites by remember { mutableStateOf(false) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = "Weather Forecast", textAlign = TextAlign.Center)
                                    Text(
                                        text = if (!showFavorites) locationName else "Favorites",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { showFavorites = !showFavorites }) {
                                    Icon(
                                        imageVector = if (showFavorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = if (showFavorites) "Back to Weather" else "View Favorites"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    if (showFavorites) {
                        // Favorites View
                        FavoritesView(
                            favorites = favoritesList,
                            onRemoveFavorite = { weatherViewModel.removeFavorite(it) },
                            onSelectFavorite = {
                                val (lat, lon) = it.split(",").map { coord -> coord.toFloat() }
                                weatherViewModel.fetchWeather(lat, lon)
                                showFavorites = false // Go back to main view
                            },
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        )
                    } else {
                        // Main Weather View
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        ) {
                            // Display connection status message
                            if (!isConnected) {
                                Text(
                                    text = "No internet connection. Showing cached data.",
                                    color = Color.Red,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                )
                            }

                            // Input fields for latitude and longitude
                            WeatherInput(
                                onFetchWeather = { latitude, longitude ->
                                    weatherViewModel.fetchWeather(latitude, longitude)
                                },
                                weatherViewModel = weatherViewModel, // Pass ViewModel here
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .weight(1f)
                            )

                            // Weather list display
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
}
