package com.example.weather_api.core_data.mappers

import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.Location
import com.example.weather_api.core_db.room.entitity.LocationDB


fun LocationDB.toLocation() = Location(
    city = city,
    coordinates = Coordinates(lon, lat),
    isFavorite = true
)

fun Location.toDB() = LocationDB(
    city = city,
    lon = coordinates.lon,
    lat = coordinates.lat,
)

