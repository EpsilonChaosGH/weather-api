package com.example.weather_api.app.screens.main.weather

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.*
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.core_data.EmptyFieldException
import com.example.weather_api.core_data.WeatherRepository
import com.example.weather_api.core_data.mappers.toMainWeatherState
import com.example.weather_api.core_data.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    logger: Logger
) : BaseViewModel(weatherRepository, logger) {


    private val _mainWeatherState: MutableStateFlow<MainWeatherState?> =
        MutableStateFlow(null)
    val mainWeatherState: StateFlow<MainWeatherState?> = _mainWeatherState.asStateFlow()

    init {
        listenCurrentState()
        refreshCurrentMainWeather()
    }

    fun refreshCurrentMainWeather() {
        viewModelScope.safeLaunch {
            _mainWeatherState.value = _mainWeatherState.value?.copy(refreshState = true)
            weatherRepository.refreshFavorites()
            _mainWeatherState.value = _mainWeatherState.value?.copy(refreshState = false)
        }
    }

    fun getMainWeatherByCity(city: String) {
        viewModelScope.safeLaunch {
            showProgress()
            weatherRepository.getMainWeatherByCity(city)
            hideProgress()
        }
    }

    fun getMainWeatherByCoordinate(coordinates: Coordinates) {
        viewModelScope.safeLaunch {
            showProgress()
            weatherRepository.getMainWeatherByCoordinates(coordinates)
            hideProgress()
        }
    }

    fun addOrRemoveFromFavorite() {
        viewModelScope.safeLaunch {
            _mainWeatherState.value?.let { value ->
                if (value.isFavorites) {
                    weatherRepository.deleteFromFavoritesByCity(value.city)
                } else {
                    weatherRepository.addToFavoritesByCity(value.city)
                }
            }
        }
    }

    fun emptyFieldException(e: EmptyFieldException) {
        _mainWeatherState.value =
            _mainWeatherState.value?.copy(emptyCityError = e.field == Field.CITY)
    }

    private fun listenCurrentState() {
        viewModelScope.safeLaunch {
            weatherRepository.listenMainWeather().collect { mainWeather ->
                if (mainWeather != null) {
                    _mainWeatherState.value = mainWeather.toMainWeatherState()
                }
            }
        }
    }

    private fun showProgress() {
        _mainWeatherState.value = _mainWeatherState.value?.copy(weatherInProgress = true)
    }

    private fun hideProgress() {
        _mainWeatherState.value = _mainWeatherState.value?.copy(weatherInProgress = false)
    }
}