package com.example.weatherapp.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherData

class WeatherAdapter :
    ListAdapter<WeatherData, WeatherAdapter.WeatherViewHolder>(WeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.weather_item, parent, false)
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateText: TextView = itemView.findViewById(R.id.weatherDate)
        private val tempText: TextView = itemView.findViewById(R.id.weatherTemperature)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)

        fun bind(weather: WeatherData) {
            dateText.text = weather.date
            tempText.text = "${weather.temperature}°C"

            // Dynamically set the weather icon based on the cloud coverage condition
            val iconResId = when (weather.cloudCoverage) {
                "Cloudy" -> R.drawable.ic_cloudy // Replace with actual resource
                "Partly Cloudy" -> R.drawable.ic_partlycloudy // Replace with actual resource
                "Sunny" -> R.drawable.ic_sunny // Replace with actual resource
                "Rainy" -> R.drawable.ic_rain // Replace with actual resource
                else -> R.drawable.ic_default // Fallback icon
            }
            weatherIcon.setImageResource(iconResId)
        }
    }
}

class WeatherDiffCallback : DiffUtil.ItemCallback<WeatherData>() {
    override fun areItemsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: WeatherData, newItem: WeatherData): Boolean {
        return oldItem == newItem
    }
}
