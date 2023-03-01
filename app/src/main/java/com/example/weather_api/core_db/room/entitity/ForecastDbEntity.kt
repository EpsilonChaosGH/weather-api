package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "forecast",
    indices = [
        Index("city", unique = true)
    ]
)
class ForecastDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "temperature") val temperature: Double,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "data") val data: Long,
)
