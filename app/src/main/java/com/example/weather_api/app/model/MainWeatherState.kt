package com.example.weather_api.app.model


data class MainWeatherState(
    val city: String,
    val isCurrent: Boolean,
    val isFavorites: Boolean,
    val weatherState: WeatherState,
    val airState: AirState,
    val forecastState: List<ForecastState>,
    val emptyCityError: Boolean = false,
    val weatherInProgress: Boolean = false,
    val refreshState: Boolean = false,
) {
    val showProgress: Boolean get() = weatherInProgress
    val enableViews: Boolean get() = !weatherInProgress
}