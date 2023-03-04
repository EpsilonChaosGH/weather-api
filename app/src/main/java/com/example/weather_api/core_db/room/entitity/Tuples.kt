package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class UpdateFavoritesTuple(
    @ColumnInfo(name = "weather_city") @PrimaryKey val city: String,
    @ColumnInfo(name = "is_favorites") var isFavorites: Boolean,
)

data class UpdateCurrentTuple(
    @ColumnInfo(name = "weather_city") @PrimaryKey val city: String,
    @ColumnInfo(name = "is_current") var isCurrent: Boolean,
)