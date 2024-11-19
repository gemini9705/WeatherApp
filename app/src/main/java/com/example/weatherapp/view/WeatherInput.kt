package com.example.weatherapp.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

//(59.3293f, 18.0686f) // Example: Stockholm

@Composable
fun WeatherInput(onFetchWeather: (Float, Float) -> Unit) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Enter Latitude:")
        TextField(
            value = latitude,
            onValueChange = { latitude = it },
            placeholder = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Enter Longitude:")
        TextField(
            value = longitude,
            onValueChange = { longitude = it },
            placeholder = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            Text("Fetch Weather")
        }
    }
}
