package com.example.weather_api.app.screens.main.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.Singletons
import com.example.weather_api.app.model.EmptyFieldException
import com.example.weather_api.app.model.Field
import com.example.weather_api.app.model.main.WeatherRepository
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.*
import com.example.weather_api.app.utils.logger.LogCatLogger
import com.example.weather_api.app.utils.logger.Logger

class WeatherViewModel(
    weatherRepository: WeatherRepository = Singletons.weatherRepository,
    logger: Logger = LogCatLogger
) : BaseViewModel(weatherRepository, logger) {

    private val _state = MutableLiveData(State("0"))
    val state = _state.share()


    fun getWeatherByCity(city: City) = viewModelScope.safeLaunch {
        showProgress()
        try {
            _state.value =
                _state.requireValue().copy(weather = weatherRepository.getWeatherByCity(city))
        } catch (e: EmptyFieldException) {
            processEmptyFieldException(e)
        } finally {
            hideProgress()
        }
    }

    private fun processEmptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyCityError = e.field == Field.City
        )
    }

    private fun showProgress() {
        _state.value = _state.requireValue().copy(emptyCityError = false, weatherInProgress = true)
    }

    private fun hideProgress() {
        _state.value = _state.requireValue().copy(weatherInProgress = false)
    }

    data class State(
        val weather: String,
        val emptyCityError: Boolean = false,
        val weatherInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = weatherInProgress
        val enableViews: Boolean get() = !weatherInProgress
    }
}