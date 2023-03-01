package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "last_air",
    indices = [
        Index("city", unique = true)
    ]
)
class LastAirDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "last_weather_key")
    val lastWeatherKey: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "no2") val no2: Double,
    @ColumnInfo(name = "o3") val o3: Double,
    @ColumnInfo(name = "pm10") val pm10: Double,
    @ColumnInfo(name = "pm25") val pm25: Double,
)