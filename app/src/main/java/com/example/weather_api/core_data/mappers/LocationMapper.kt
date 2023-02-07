package com.example.weather_api.core_data.mappers

import com.example.weather_api.app.model.Const
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.Location
import com.example.weather_api.core_db.room.entitity.LastLocationDbEntity
import com.example.weather_api.core_db.room.entitity.LocationDbEntity

fun LocationDbEntity.toLocation() = Location(
    city = city,
    coordinates = Coordinates(lon, lat),
    isFavorite = true
)

fun Location.toLocationDB() = LocationDbEntity(
    city = city,
    lon = coordinates.lon,
    lat = coordinates.lat,
)

fun LastLocationDbEntity.toLocation() = Location(
    city = city,
    coordinates = Coordinates(lon, lat),
    isFavorite = true
)

fun Location.toLastDB() = LastLocationDbEntity(
    lastLocationKey = Const.LAST_LOCATION_KEY,
    city = city,
    lon = coordinates.lon,
    lat = coordinates.lat,
)

