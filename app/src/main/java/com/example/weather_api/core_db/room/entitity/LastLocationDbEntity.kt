package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "last_location")
class LastLocationDbEntity(
    @PrimaryKey
    @ColumnInfo(name = "last_location_key")
    val lastLocationKey: String,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "lon") val lon: String,
    @ColumnInfo(name = "lat") val lat: String,
)
