package com.example.weather_api.app.screens.main.favorites

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather_api.R
import com.example.weather_api.app.model.WeatherState
import com.example.weather_api.databinding.ItemFavoriteBinding

interface FavoritesClickListener {
    fun deleteFromFavorites(city: String)
    fun showDetailsWeather(city: String)
}

class FavoriteAdapter(
    private val listener: FavoritesClickListener
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>(), View.OnClickListener {

    class FavoriteViewHolder(
        val binding: ItemFavoriteBinding
    ) : RecyclerView.ViewHolder(binding.root)

    var favoritesList = listOf<WeatherState>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onClick(v: View) {
        val favorite = v.tag as WeatherState

        when (v.id) {
            R.id.favoriteImageView -> listener.deleteFromFavorites(favorite.city)
            R.id.mainWeatherContainer -> listener.showDetailsWeather(favorite.city)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavoriteBinding.inflate(inflater, parent, false)
        binding.favoriteImageView.setOnClickListener(this)
        binding.mainWeatherContainer.setOnClickListener(this)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val weather = favoritesList[position]

        with(holder.binding) {
            favoriteImageView.tag = weather
            mainWeatherContainer.tag = weather
            cityNameTextView.text = weather.city
            temperatureTextView.text = weather.temperature
            currentDateTextView.text = weather.data
            currentWeatherTextView.text = weather.description
            weatherIconImageView.setImageResource(weather.weatherType.iconResId)

            if (weather.isFavorites) {
                favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_border_24)
            } else {
                favoriteImageView.setImageResource(R.drawable.ic_baseline_favorite_24)
            }
        }
    }

    override fun getItemCount() = favoritesList.size
}