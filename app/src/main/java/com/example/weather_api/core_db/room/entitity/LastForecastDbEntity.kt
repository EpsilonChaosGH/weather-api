package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "last_forecast")
class LastForecastDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "data") val data: Long,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "icon") val icon: String,
)
