package com.example.weather_api.core_db.room.entitity

import androidx.room.*

@Entity(
    tableName = "forecast",
    indices = [
        Index("city_forecast")
    ],
    foreignKeys = [
        ForeignKey(
            entity = MainWeatherDbEntity::class,
            parentColumns = ["weather_city"],
            childColumns = ["city_forecast"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ForecastDbEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "city_forecast") val city: String,
    @ColumnInfo(name = "temperature_forecast") val temperature: Double,
    @ColumnInfo(name = "icon_forecast") val icon: String,
    @ColumnInfo(name = "data_forecast") val data: Long,
)