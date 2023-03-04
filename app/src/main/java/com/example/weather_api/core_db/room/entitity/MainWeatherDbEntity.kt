package com.example.weather_api.core_db.room.entitity

import androidx.room.*

@Entity(
    tableName = "weather",
//    indices = [
//        Index("weather_city", unique = true)
//    ],
//    foreignKeys = [
//        ForeignKey(
//            entity = ForecastDbEntity::class,
//            parentColumns = ["city_forecast"],
//            childColumns = ["weather_city"],
//            onDelete = ForeignKey.CASCADE,
//            onUpdate = ForeignKey.CASCADE
//        )
//    ]
)
data class MainWeatherDbEntity(
    @PrimaryKey @ColumnInfo(name = "weather_city") val weatherCity: String,
    @ColumnInfo(name = "is_current") var isCurrent: Boolean,
    @ColumnInfo(name = "is_favorites") var isFavorites: Boolean,
    @Embedded val weather: WeatherDbEntity,
    @Embedded val air: AirDbEntity
)