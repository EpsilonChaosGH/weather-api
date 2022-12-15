package com.example.weather_api.app.screens.main.weather

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.model.EmptyFieldException
import com.example.weather_api.app.model.Field
import com.example.weather_api.app.model.main.WeatherRepository
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.app.model.main.entities.WeatherEntity
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.*
import com.example.weather_api.app.utils.logger.Logger
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

    private val _forecastState = MutableLiveData<List<WeatherEntity>>()
    val forecastState = _forecastState.share()

    init {
        getWeatherAndWeatherForecastByCity(City(weatherRepository.getCurrentCity()))
    }

    fun getWeatherAndWeatherForecastByCity(city: City) =
        viewModelScope.launch {
            showProgress()
            val weatherJob = safeLaunch {
                try {
                    getWeatherByCity(city)
                } catch (e: EmptyFieldException) {
                    emptyFieldException(e)
                }
            }
            val weatherForecastJob = safeLaunch {
                try {
                    getWeatherForecastByCity(city)
                } catch (e: EmptyFieldException) {
                    emptyFieldException(e)
                }
            }
            weatherJob.join()
            weatherForecastJob.join()
            hideProgress()
        }

    fun getWeatherAndWeatherForecastByCoordinate(coordinates: Coordinates) =
        viewModelScope.launch() {
            showProgress()
            val weatherJob = safeLaunch {
                getWeatherByCoordinates(coordinates)
            }
            val weatherForecastJob = safeLaunch {
                getWeatherForecastByCoordinates(coordinates)
            }
            weatherJob.join()
            weatherForecastJob.join()
            hideProgress()
        }

    private suspend fun getWeatherByCity(city: City) {
        val response = weatherRepository.getWeatherByCity(city)
        setState(response)
        weatherRepository.setCurrentCity(response.cityName)
    }

    private suspend fun getWeatherForecastByCity(city: City) {
        val response = weatherRepository.getWeatherForecastByCity(city)
        _forecastState.value = response
    }

    private suspend fun getWeatherByCoordinates(coordinates: Coordinates) {
        val response = weatherRepository.getWeatherByCoordinates(coordinates)
        setState(response)
        weatherRepository.setCurrentCity(response.cityName)
    }

    private suspend fun getWeatherForecastByCoordinates(coordinates: Coordinates) {
        val response = weatherRepository.getWeatherForecastByCoordinates(coordinates)
        _forecastState.value = response
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

    private fun emptyFieldException(e: EmptyFieldException) {
        _state.value = _state.requireValue().copy(
            emptyCityError = e.field == Field.City
        )
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
            date = dataToString(weather.data)
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
        val description: String = "...",
        val mainWeather: String = "...",
        val feelsLike: String = "0.0",
        val humidity: String = "0.0",
        val pressure: String = "0.0",
        val windSpeed: String = "0.0",
        val date: String = "...",
        val emptyCityError: Boolean = false,
        val weatherInProgress: Boolean = false
    ) {
        val showProgress: Boolean get() = weatherInProgress
        val enableViews: Boolean get() = !weatherInProgress
    }
}