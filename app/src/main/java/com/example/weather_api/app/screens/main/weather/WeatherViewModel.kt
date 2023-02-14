package com.example.weather_api.app.screens.main.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.AirQuality
import com.example.weather_api.app.model.Field
import com.example.weather_api.core_data.models.AirPollutionEntity
import com.example.weather_api.core_data.models.City
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.WeatherEntity
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.*
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.core_data.EmptyFieldException
import com.example.weather_api.core_data.WeatherRepository
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

    private val _forecastState = MutableLiveData<List<WeatherEntity>>()
    val forecastState = _forecastState.share()

    private val _airState = MutableLiveData(AirState())
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
                _weatherState.value = WeatherState(weather = weather)
                _hideVeilEvent.publishEvent()
            }
        }
        viewModelScope.launch {
            weatherRepository.listenCurrentForecastState().collect { forecast ->
                _forecastState.value = forecast
            }
        }
        viewModelScope.launch {
            weatherRepository.listenCurrentAirPollutionState().collect { airPollution ->
                setAirState(airPollution)
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
            if (weatherState.value!!.weather.location.isFavorite) {
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

    private fun setAirState(airPollutionEntity: AirPollutionEntity) {
        _airState.value = _airState.requireValue().copy(
            no2 = airPollutionEntity.no2.toString(),
            no2Quality = airPollutionEntity.no2Quality,
            pm10 = airPollutionEntity.pm10.toString(),
            pm10Quality = airPollutionEntity.pm10Quality,
            o3 = airPollutionEntity.o3.toString(),
            o3Quality = airPollutionEntity.o3Quality,
            pm25 = airPollutionEntity.pm2_5.toString(),
            pm25Quality = airPollutionEntity.pm2_5Quality
        )
    }

    private fun showProgress() {
        _weatherState.value =
            _weatherState.requireValue().copy(emptyCityError = false, weatherInProgress = true)
    }

    private fun hideProgress() {
        _weatherState.value = _weatherState.requireValue().copy(weatherInProgress = false)
    }

    data class AirState(
        val no2: String = "...",
        val no2Quality: AirQuality = AirQuality.Error,
        val pm10: String = "...",
        val pm10Quality: AirQuality = AirQuality.Error,
        val o3: String = "...",
        val o3Quality: AirQuality = AirQuality.Error,
        val pm25: String = "...",
        val pm25Quality: AirQuality = AirQuality.Error,
    )

    data class WeatherState(
        val weather: WeatherEntity,
        val emptyCityError: Boolean = false,
        val weatherInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = weatherInProgress
        val enableViews: Boolean get() = !weatherInProgress
    }
}