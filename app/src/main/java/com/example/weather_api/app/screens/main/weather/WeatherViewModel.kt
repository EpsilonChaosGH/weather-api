package com.example.weather_api.app.screens.main.weather

import android.annotation.SuppressLint
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
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    weatherRepository: WeatherRepository,
    logger: Logger
) : BaseViewModel(weatherRepository, logger) {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _airState = MutableLiveData(AirState())
    val airState = _airState.share()

    private val _forecastState = MutableLiveData<List<WeatherEntity>>()
    val forecastState = _forecastState.share()

    init {
        listenCurrentState()
    }

    private fun listenCurrentState() {
        viewModelScope.launch {
            weatherRepository.listenCurrentWeatherState().collect { weather ->
                setState(weather)
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

    fun getWeatherAndForecastAndAirByCoordinate(coordinates: Coordinates) =
        viewModelScope.launch {
            showProgress()
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

    fun addOrRemoveToFavorite() {
        viewModelScope.safeLaunch {
            if (state.value!!.isFavorite) {
                weatherRepository.removeFromFavorites()
            } else {
                weatherRepository.addToFavorites()
            }
        }
    }

    fun emptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyCityError = e.field == Field.City
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun dataToString(data: Date): String {
        return try {
            val sdf = SimpleDateFormat("EEE, d MMMM HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(data)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun setState(weather: WeatherEntity) {
        _state.value = _state.requireValue().copy(
            cityName = weather.cityName,
            country = weather.country,
            temperature = weather.temperature.toString(),
            mainWeather = weather.mainWeather,
            description = weather.description,
            feelsLike = weather.feelsLike.toString(),
            humidity = weather.humidity.toString(),
            pressure = weather.pressure.toString(),
            windSpeed = weather.windSpeed.toString(),
            date = dataToString(weather.data),
            isFavorite = weather.location.isFavorite
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
        _state.value = _state.requireValue().copy(emptyCityError = false, weatherInProgress = true)
    }

    private fun hideProgress() {
        _state.value = _state.requireValue().copy(weatherInProgress = false)
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

    data class State(
        val cityName: String = "...",
        val country: String = "...",
        val temperature: String = "0.0",
        val description: String = "...",
        val mainWeather: String = "...",
        val feelsLike: String = "0.0",
        val humidity: String = "0.0",
        val pressure: String = "0.0",
        val windSpeed: String = "0.0",
        val date: String = "...",
        val isFavorite: Boolean = false,
        val emptyCityError: Boolean = false,
        val weatherInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = weatherInProgress
        val enableViews: Boolean get() = !weatherInProgress
    }
}