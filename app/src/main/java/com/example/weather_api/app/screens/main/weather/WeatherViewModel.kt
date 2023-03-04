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

    private val _showVeilEvent = MutableUnitLiveEvent()
    val showVeilEvent = _showVeilEvent.share()

    private val _hideVeilEvent = MutableUnitLiveEvent()
    val hideVeilEvent = _hideVeilEvent.share()

    init {
        listenCurrentState()
    }

    private fun listenCurrentState() {
        viewModelScope.launch {
//            _showVeilEvent.publishEvent()
            weatherRepository.listenCurrentWeatherState().collect { weather ->
                if (weather != null) {
                    _weatherState.value = weather.weatherEntity.toWeatherState(
                        FORMAT_EEE_d_MMMM_HH_mm,
                        weather.isFavorites
                    )
                    _forecastState.value =
                        weather.forecastEntityList.map { it.toForecastState(FORMAT_EEE_HH_mm) }
                    _airState.value = weather.airEntity.toAirPollutionState()
                }
            }
        }
    }

    fun getWeatherAndForecastAndAirByCity(city: String) {
        viewModelScope.launch {
            showProgress()
            val weatherJob = safeLaunch {
                weatherRepository.getWeatherByCity(city)
            }
            weatherJob.join()
            hideProgress()
        }
    }

    fun getWeatherAndForecastAndAirByCoordinate(coordinates: Coordinates) {
        // showProgress()
        viewModelScope.launch {
            val weatherJob = safeLaunch {
                weatherRepository.getWeatherByCoordinates(coordinates)
            }
            weatherJob.join()
            //  hideProgress()
        }
    }

    fun addOrRemoveFromFavorite() {
        viewModelScope.safeLaunch {
            if (weatherState.value!!.isFavorites) {
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