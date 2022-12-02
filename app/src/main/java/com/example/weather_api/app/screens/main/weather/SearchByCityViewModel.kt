package com.example.weather_api.app.screens.main.weather

import androidx.lifecycle.MutableLiveData
import com.example.weather_api.app.model.EmptyFieldException
import com.example.weather_api.app.model.Field
import com.example.weather_api.app.model.main.WeatherRepository
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.MutableUnitLiveEvent
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.app.utils.publishEvent
import com.example.weather_api.app.utils.requireValue
import com.example.weather_api.app.utils.share

class SearchByCityViewModel(
    weatherRepository: WeatherRepository = com.example.weather_api.app.Singletons.weatherRepository,
    logger: Logger = com.example.weather_api.app.utils.logger.LogCatLogger
) : BaseViewModel(weatherRepository, logger) {

    private val _state = MutableLiveData<State>()
    val state = _state.share()

    private val _goBackEvent = MutableUnitLiveEvent()
    val goBackEvent = _goBackEvent.share()

    fun searchButtonPressed(city: String) {
        _state.value = State()
        try {
            weatherRepository.setCurrentCity(city)
            goBack()
        } catch (e: EmptyFieldException) {
            emptyField(e)
        }
    }

    private fun emptyField(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyCityError = e.field == Field.City
        )
    }

    private fun goBack() = _goBackEvent.publishEvent()

    data class State(
        val emptyCityError: Boolean = false,
    )
}