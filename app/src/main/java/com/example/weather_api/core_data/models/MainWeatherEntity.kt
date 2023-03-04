package com.example.weather_api.core_data.models

data class MainWeatherEntity(
    var city: String,
    var isCurrent: Boolean,
    var isFavorites: Boolean,
    var weatherEntity: WeatherEntity,
    var airEntity: AirEntity,
    var forecastEntityList: List<ForecastEntity>,
)