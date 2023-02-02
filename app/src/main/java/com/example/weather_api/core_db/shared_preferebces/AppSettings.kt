package com.example.weather_api.core_db.shared_preferebces

import com.example.weather_api.core_data.models.Location

interface AppSettings {

    fun getCurrentLocation(): Location

    fun setCurrentLocation(location: Location)

}