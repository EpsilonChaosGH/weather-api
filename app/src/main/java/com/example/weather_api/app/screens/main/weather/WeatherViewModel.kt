package com.example.weather_api.app.screens.main.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.*
import com.example.weather_api.app.utils.*
import com.example.weather_api.core_data.*
import com.example.weather_api.core_data.mappers.toMainWeatherState
import com.example.weather_api.core_data.models.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
) : ViewModel() {

    private val _showErrorMessageResEvent = MutableStateFlow<Event<Int?>>(Event(null))
    val showErrorMessageResEvent = _showErrorMessageResEvent.asStateFlow()

    private val _mainWeatherState: MutableStateFlow<MainWeatherState?> =
        MutableStateFlow(null)
    val mainWeatherState: StateFlow<MainWeatherState?> = _mainWeatherState.asStateFlow()

    init {
        listenCurrentState()
        refreshCurrentMainWeather()
    }

    fun showToast(message: Int) {
        _showErrorMessageResEvent.value = Event(message)
    }

    fun refreshCurrentMainWeather() {
        viewModelScope.launch {
            val result = viewModelScope.safeLaunchAsync {
                _mainWeatherState.value = _mainWeatherState.value?.copy(refreshState = true)
                weatherRepository.refreshFavorites()
                _mainWeatherState.value = _mainWeatherState.value?.copy(refreshState = false)
            }
            _showErrorMessageResEvent.value = Event(result.await())
        }
    }

    fun getMainWeatherByCity(city: String) {
        viewModelScope.launch {
            showProgress()
            val result = viewModelScope.safeLaunchAsync {
                weatherRepository.getMainWeatherByCity(city)
            }
            _showErrorMessageResEvent.value = Event(result.await())
            hideProgress()
        }
    }

    fun getMainWeatherByCoordinate(coordinates: Coordinates) {
        viewModelScope.launch {
            showProgress()
            val result = viewModelScope.safeLaunchAsync {
                weatherRepository.getMainWeatherByCoordinates(coordinates)
            }
            _showErrorMessageResEvent.value = Event(result.await())
            hideProgress()
        }
    }

    fun addOrRemoveFromFavorite() {
        viewModelScope.launch {
            val result = viewModelScope.safeLaunchAsync {
                _mainWeatherState.value?.let { value ->
                    if (value.isFavorites) {
                        weatherRepository.deleteFromFavoritesByCity(value.city)
                    } else {
                        weatherRepository.addToFavoritesByCity(value.city)
                    }
                }
            }
            _showErrorMessageResEvent.value = Event(result.await())
        }
    }

    fun emptyFieldException(e: EmptyFieldException) {
        _mainWeatherState.value =
            _mainWeatherState.value?.copy(emptyCityError = e.field == Field.CITY)
    }

    private fun listenCurrentState() {
        viewModelScope.launch {
            val result = viewModelScope.safeLaunchAsync {
                weatherRepository.listenMainWeather().collect { mainWeather ->
                    if (mainWeather != null) {
                        _mainWeatherState.value = mainWeather.toMainWeatherState()
                    }
                }
            }
            _showErrorMessageResEvent.value = Event(result.await())
        }
    }

    private fun showProgress() {
        _mainWeatherState.value = _mainWeatherState.value?.copy(weatherInProgress = true)
    }

    private fun hideProgress() {
        _mainWeatherState.value = _mainWeatherState.value?.copy(weatherInProgress = false)
    }
}