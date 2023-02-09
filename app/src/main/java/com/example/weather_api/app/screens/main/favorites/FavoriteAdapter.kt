package com.example.weather_api.app.screens.main.favorites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_api.R
import com.example.weather_api.core_data.models.WeatherEntity
import com.example.weather_api.databinding.ItemFavoriteBinding
import java.text.SimpleDateFormat
import java.util.*
import java.sql.Date


class FavoriteAdapter : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(
        val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root)

    var favoritesList = listOf<WeatherEntity>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavoriteBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat")
    private fun dataToTime(data: Date): String? {
        return try {
            val sdf = SimpleDateFormat("EEE, d MMMM HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(data)
        } catch (e: Exception) {
            e.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val weather = favoritesList[position]

        with(holder.binding) {
            cityNameTextView.text = weather.cityName
            temperatureTextView.text = "${weather.temperature.toInt()}°C"
            currentDateTextView.text = dataToTime(weather.data)
            currentWeatherTextView.text = weather.description

            if (weather.location.isFavorite) {
                favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
            }

            when (weather.mainWeather) {
                "Clear" -> weatherIconImageView.setImageResource(R.drawable.ic_sun)
                "Clouds" -> weatherIconImageView.setImageResource(R.drawable.ic_cloud)
                "Rain" -> weatherIconImageView.setImageResource(R.drawable.ic_heavey_rain)
                "Snow" -> weatherIconImageView.setImageResource(R.drawable.ic_winter)
                "Mist" -> weatherIconImageView.setImageResource(R.drawable.ic_fog)
                else -> weatherIconImageView.setImageResource(R.drawable.ic_cloudy)
            }
        }
    }

    override fun getItemCount() = favoritesList.size
}