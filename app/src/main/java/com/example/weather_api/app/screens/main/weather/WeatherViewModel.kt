package com.example.weather_api.app.screens.main.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.*
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.*
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.core_data.EmptyFieldException
import com.example.weather_api.core_data.WeatherRepository
import com.example.weather_api.core_data.mappers.toAirPollutionState
import com.example.weather_api.core_data.mappers.toWeatherState
import com.example.weather_api.core_data.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    logger: Logger
) : BaseViewModel(weatherRepository, logger) {

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState = _weatherState.share()

    private val _forecastState = MutableLiveData<List<WeatherState>>()
    val forecastState = _forecastState.share()

    private val _airState = MutableLiveData<AirPollutionState>()
    val airState = _airState.share()

    private val _showVeilEvent = MutableUnitLiveEvent()
    val showVeilEvent = _showVeilEvent.share()

    private val _hideVeilEvent = MutableUnitLiveEvent()
    val hideVeilEvent = _hideVeilEvent.share()

    init {
        listenCurrentState()
    }

    private fun listenCurrentState() {
        viewModelScope.launch {
            _showVeilEvent.publishEvent()
            weatherRepository.listenCurrentWeatherState().collect { weather ->
                _weatherState.value = weather.toWeatherState(FORMAT_EEE_d_MMMM_HH_mm)
                _hideVeilEvent.publishEvent()
            }
        }
        viewModelScope.launch {
            weatherRepository.listenCurrentForecastState().collect { forecast ->
                _forecastState.value = forecast.map { it.toWeatherState(FORMAT_EEE_HH_mm) }
            }
        }
        viewModelScope.launch {
            weatherRepository.listenCurrentAirPollutionState().collect { airPollution ->
                _airState.value = airPollution.toAirPollutionState()
            }
        }
    }

    fun getWeatherAndForecastAndAirByCity(city: City) {
        viewModelScope.launch {
            showProgress()
            val weatherJob = safeLaunch {
                weatherRepository.getWeatherByCity(city)
            }
            val forecastJob = safeLaunch {
                weatherRepository.getForecastByCity(city)
            }
            val airJob = safeLaunch {
                weatherRepository.getAirPollutionByCity(city)
            }
            weatherJob.join()
            forecastJob.join()
            airJob.join()
            hideProgress()
        }
    }

    fun getWeatherAndForecastAndAirByCoordinate(coordinates: Coordinates) {
        showProgress()
        viewModelScope.launch {
            val weatherJob = safeLaunch {
                weatherRepository.getWeatherByCoordinates(coordinates)
            }
            val forecastJob = safeLaunch {
                weatherRepository.getForecastByCoordinates(coordinates)
            }
            val airJob = safeLaunch {
                weatherRepository.getAirPollutionByCoordinate(coordinates)
            }
            airJob.join()
            weatherJob.join()
            forecastJob.join()
            hideProgress()
        }
    }

    fun addOrRemoveToFavorite() {
        viewModelScope.safeLaunch {
            if (weatherState.value!!.location.isFavorite) {
                weatherRepository.deleteFromFavorites()
            } else {
                weatherRepository.addToFavorites()
            }
        }
    }

    fun emptyFieldException(e: EmptyFieldException) {
        _weatherState.value = _weatherState.requireValue().copy(
            emptyCityError = e.field == Field.City
        )
    }

    private fun showProgress() {
        _weatherState.value =
            _weatherState.requireValue().copy(emptyCityError = false, weatherInProgress = true)
    }

    private fun hideProgress() {
        _weatherState.value = _weatherState.requireValue().copy(weatherInProgress = false)
    }
}