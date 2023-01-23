package com.example.weather_api.app.model.settings

interface AppSettings {

    fun getCurrentCityName(): String

    fun setCurrentCityName(cityName: String)

}