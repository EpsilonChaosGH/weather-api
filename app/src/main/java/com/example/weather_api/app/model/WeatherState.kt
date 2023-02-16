package com.example.weather_api.app.model

import com.example.weather_api.core_data.models.Location

data class WeatherState(
    val cityName: String,
    val country: String,
    val temperature: String,
    val weatherType: WeatherType,
    val description: String,
    val feelsLike: String,
    val humidity: String,
    val pressure: String,
    val windSpeed: String,
    val data: String,
    val location: Location,
    val emptyCityError: Boolean = false,
    val weatherInProgress: Boolean = false
) {
    val showProgress: Boolean get() = weatherInProgress
    val enableViews: Boolean get() = !weatherInProgress
}