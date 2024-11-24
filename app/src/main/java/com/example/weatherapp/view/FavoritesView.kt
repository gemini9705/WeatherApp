package com.example.weatherapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.unit.dp


@Composable
fun FavoritesView(
    favorites: List<String>,
    onRemoveFavorite: (String) -> Unit,
    onSelectFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.padding(16.dp)) {
        items(favorites) { favorite ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = favorite)
                Row {
                    Button(onClick = { onSelectFavorite(favorite) }) {
                        Text("View")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onRemoveFavorite(favorite) }) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}
