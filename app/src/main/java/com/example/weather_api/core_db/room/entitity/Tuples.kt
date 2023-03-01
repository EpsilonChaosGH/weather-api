package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class WeatherUpdateFavoritesTuple(
    @ColumnInfo(name = "last_weather_key") @PrimaryKey val lastWeatherKey: String,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean,
)

data class UpdateFavoritesTuple(
    @ColumnInfo(name = "city") @PrimaryKey val city: String,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean,
)