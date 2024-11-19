package com.example.weatherapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//(59.3293f, 18.0686f) // Example: Stockholm

@Composable
fun WeatherInput(
    onFetchWeather: (Float, Float) -> Unit,
    modifier: Modifier = Modifier // Add the modifier parameter with a default value
) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = modifier // Use the passed modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    placeholder = { Text("Latitude") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    placeholder = { Text("Longitude") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val lat = latitude.toFloatOrNull()
                    val lon = longitude.toFloatOrNull()
                    if (lat != null && lon != null) {
                        onFetchWeather(lat, lon)
                    } else {
                        Toast.makeText(
                            context,
                            "Please enter valid numbers for latitude and longitude.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("FETCH")
            }
        }
    }
}

