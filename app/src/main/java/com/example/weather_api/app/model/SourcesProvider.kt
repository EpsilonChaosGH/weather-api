package com.example.weather_api.app.model

import com.example.weather_api.app.model.main.WeatherSource

interface SourcesProvider {

    fun getWeatherSource(): WeatherSource

}