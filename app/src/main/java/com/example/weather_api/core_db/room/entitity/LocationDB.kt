package com.example.weather_api.core_db.room.entitity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_locations")
data class LocationDB(
    @PrimaryKey val city: String,
    @ColumnInfo(name = "lon") val lon: String,
    @ColumnInfo(name = "lat") val lat: String,
){

}