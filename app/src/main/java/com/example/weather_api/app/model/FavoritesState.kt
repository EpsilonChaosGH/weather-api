package com.example.weather_api.app.model

data class FavoritesState(
    val favorites: List<WeatherState>,
    val emptyListState: Boolean = false,
    val refreshState: Boolean = false,
)
