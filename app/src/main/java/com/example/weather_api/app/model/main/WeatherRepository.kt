package com.example.weather_api.app.model.main

import com.example.weather_api.app.model.*
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.app.model.settings.AppSettings
import com.example.weather_api.app.model.wrapBackendExceptions
import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity

class WeatherRepository(
    private val weatherSource: WeatherSource,
    private val appSettings: AppSettings
) {

    fun getCurrentCity(): String = appSettings.getCurrentCityName()

    fun setCurrentCity(cityName: String) {
        if (cityName.isBlank()) throw EmptyFieldException(Field.City)
        appSettings.setCurrentCityName(cityName)
    }

    suspend fun getWeatherByCity(city: City): GetWeatherResponseEntity = wrapBackendExceptions {
        if (city.cityName.isBlank()) throw EmptyFieldException(Field.City)
        try {
            return weatherSource.getWeatherByCity(city)
        } catch (e: BackendException) {
            if (e.code == 404) throw CityNotFoundException(e)
            else throw e
        }
    }

    suspend fun getWeatherByCoordinates(coordinates: Coordinates): GetWeatherResponseEntity =
        wrapBackendExceptions {
            try {
                return weatherSource.getWeatherByCoordinates(coordinates)
            } catch (e: Exception) {
                throw e
            }
        }
}