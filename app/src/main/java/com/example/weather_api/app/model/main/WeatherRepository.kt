package com.example.weather_api.app.model.main

import com.example.weather_api.app.model.*
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.wrapBackendExceptions

class WeatherRepository(
    private val weatherSource: WeatherSource
) {

    suspend fun getWeatherByCity(city: City): String = wrapBackendExceptions {
        if (city.cityName.isBlank()) throw EmptyFieldException(Field.City)
        try {
            return weatherSource.getWeatherByCity(city)
        } catch (e: BackendException) {
            if (e.code == 404) throw CityNotFoundException(e)
            else throw e
        }
    }
}