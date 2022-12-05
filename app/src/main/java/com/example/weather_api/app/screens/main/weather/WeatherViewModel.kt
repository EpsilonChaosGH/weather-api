package com.example.weather_api.app.screens.main.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.Singletons
import com.example.weather_api.app.model.EmptyFieldException
import com.example.weather_api.app.model.Field
import com.example.weather_api.app.model.main.WeatherRepository
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.*
import com.example.weather_api.app.utils.logger.LogCatLogger
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity

class WeatherViewModel(
    weatherRepository: WeatherRepository = Singletons.weatherRepository,
    logger: Logger = LogCatLogger
) : BaseViewModel(weatherRepository, logger) {

    private val _state = MutableLiveData(State())
    val state = _state.share()


    init {
        getWeatherByCity(weatherRepository.getCurrentCity())
    }

    fun getWeatherByCity(city: String) = viewModelScope.safeLaunch {
        showProgress()
        try {
            val response = weatherRepository.getWeatherByCity(City(city))
            setState(response)
            weatherRepository.setCurrentCity(response.name)
        } catch (e: EmptyFieldException) {
            emptyFieldException(e)
        } finally {
            hideProgress()
        }
    }

    fun getWeatherByCoordinates(coordinates: Coordinates) = viewModelScope.safeLaunch {
        showProgress()
        try {
            val response = weatherRepository.getWeatherByCoordinates(coordinates)
            setState(response)
            weatherRepository.setCurrentCity(response.name)
        } finally {
            hideProgress()
        }
    }

    private fun emptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyCityError = e.field == Field.City
        )
    }

    private fun setState(weather: GetWeatherResponseEntity) {
        _state.value = _state.requireValue().copy(
            cityName = weather.name,
            country = weather.sys.country,
            temperature = weather.main.temp.toString(),
            mainWeather = weather.weather[0].description,
            feelsLike = weather.main.feelsLike.toString(),
            humidity = weather.main.humidity.toString(),
            pressure = weather.main.pressure.toString(),
            windSpeed = weather.wind.speed.toString()
        )
    }

    private fun showProgress() {
        _state.value = _state.requireValue().copy(emptyCityError = false, weatherInProgress = true)
    }

    private fun hideProgress() {
        _state.value = _state.requireValue().copy(weatherInProgress = false)
    }

    data class State(
        val cityName: String = "...",
        val country: String = "...",
        val temperature: String = "0.0",
        val mainWeather: String = "...",
        val feelsLike: String = "0.0",
        val humidity: String = "0.0",
        val pressure: String = "0.0",
        val windSpeed: String = "0.0",
        val emptyCityError: Boolean = false,
        val weatherInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = weatherInProgress
        val enableViews: Boolean get() = !weatherInProgress
    }
}