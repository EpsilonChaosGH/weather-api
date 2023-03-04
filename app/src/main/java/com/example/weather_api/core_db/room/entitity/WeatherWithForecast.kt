package com.example.weather_api.core_db.room.entitity

import androidx.room.Embedded
import androidx.room.Relation

data class WeatherWithForecast(
    @Embedded val weather: MainWeatherDbEntity,
    @Relation(
        parentColumn = "weather_city",
        entityColumn = "city_forecast"
    )
    val forecastList: List<ForecastDbEntity>
)