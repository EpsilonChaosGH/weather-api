package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    indices = [
        Index("city", unique = true)
    ]
)
class FavoritesDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "feels_like") val feelsLike: Double,
    @ColumnInfo(name = "humidity") val humidity: Long,
    @ColumnInfo(name = "pressure") val pressure: Long,
    @ColumnInfo(name = "wind_speed") val windSpeed: Double,
    @ColumnInfo(name = "data") val data: Long,
    @ColumnInfo(name = "lon") val lon: String,
    @ColumnInfo(name = "lat") val lat: String,
    @ColumnInfo(name = "is_favorite") var isFavorite: Boolean,
)