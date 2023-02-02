package com.example.weather_api.core_data.models


data class Location(
    val city: String,
    val coordinates: Coordinates,
    var isFavorite: Boolean = false
)
