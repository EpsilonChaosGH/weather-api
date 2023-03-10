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
import com.example.weather_api.core_data.mappers.toForecastState
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

    private val _forecastState = MutableLiveData<List<ForecastState>>()
    val forecastState = _forecastState.share()

    private val _airState = MutableLiveData<AirState>()
    val airState = _airState.share()

    init {
        listenCurrentState()
        viewModelScope.safeLaunch {
            refreshCurrentMainWeather()
        }
    }

    suspend fun refreshCurrentMainWeather() {
        viewModelScope.safeLaunch {
            weatherRepository.refreshCurrentMainWeather()
        }.join()
    }

    private fun listenCurrentState() {
        viewModelScope.launch {
            weatherRepository.listenMainWeather().collect { weather ->
                if (weather != null) {
                    _weatherState.value = weather.weatherEntity.toWeatherState(
                        isFavorites = weather.isFavorites
                    )
                    _forecastState.value =
                        weather.forecastEntityList.map {
                            it.toForecastState(
                                FORMAT_EEE_HH_mm,
                                weather.weatherEntity.timezone
                            )
                        }
                    _airState.value = weather.airEntity.toAirPollutionState()
                }
            }
        }
    }

    fun getMainWeatherByCity(city: String) {
        viewModelScope.launch {
            showProgress()
            safeLaunch {
                weatherRepository.getMainWeatherByCity(city)
            }.join()
            hideProgress()
        }
    }

    fun getMainWeatherByCoordinate(coordinates: Coordinates) {
        showProgress()
        viewModelScope.launch {
            safeLaunch {
                weatherRepository.getMainWeatherByCoordinates(coordinates)
            }.join()
            hideProgress()
        }
    }

    fun addOrRemoveFromFavorite() {
        viewModelScope.safeLaunch {
            if (weatherState.value!!.isFavorites) {
                weatherRepository.deleteFromFavoritesByCity(_weatherState.value?.city ?: "")
            } else {
                weatherRepository.addToFavoritesByCity(_weatherState.value?.city ?: "")
            }
        }
    }

    fun emptyFieldException(e: EmptyFieldException) {
        _weatherState.value = _weatherState.requireValue().copy(
            emptyCityError = e.field == Field.City
        )
    }

    private fun showProgress() {
        if (_weatherState.value == null) return
        _weatherState.value =
            _weatherState.requireValue().copy(emptyCityError = false, weatherInProgress = true)
    }

    private fun hideProgress() {
        if (_weatherState.value == null) return
        _weatherState.value = _weatherState.requireValue().copy(weatherInProgress = false)
    }
}