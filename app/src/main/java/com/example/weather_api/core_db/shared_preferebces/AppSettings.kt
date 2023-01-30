package com.example.weather_api.core_db.shared_preferebces

import com.example.weather_api.core_data.models.Coordinates

interface AppSettings {

    fun getCurrentCoordinates(): Coordinates

    fun setCurrentCoordinates(coordinates: Coordinates)

}