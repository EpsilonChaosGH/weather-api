package com.example.weather_api.app.screens.main.weather

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.weather_api.app.Singletons
import com.example.weather_api.app.model.EmptyFieldException
import com.example.weather_api.app.model.Field
import com.example.weather_api.app.model.main.WeatherRepository
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.app.model.main.entities.WeatherEntity
import com.example.weather_api.app.screens.base.BaseViewModel
import com.example.weather_api.app.utils.*
import com.example.weather_api.app.utils.logger.LogCatLogger
import com.example.weather_api.app.utils.logger.Logger
import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.*

class WeatherViewModel(
    weatherRepository: WeatherRepository = Singletons.weatherRepository,
    logger: Logger = LogCatLogger
) : BaseViewModel(weatherRepository, logger) {

    private val _state = MutableLiveData(State())
    val state = _state.share()

    private val _forecastState = MutableLiveData<List<WeatherEntity>>()
    val forecastState = _forecastState.share()

    init {
        getWeatherByCity(City(weatherRepository.getCurrentCity()))
    }

    fun getWeatherByCity(city: City) = viewModelScope.safeLaunch {
        showProgress()
        try {
            val response = weatherRepository.getWeatherByCity(city)
            getWeatherForecastByCity(city)
            setState(response)
            weatherRepository.setCurrentCity(response.cityName)
        } catch (e: EmptyFieldException) {
            emptyFieldException(e)
        } finally {
            hideProgress()
        }
    }

    fun getWeatherByCoordinates(coordinates: Coordinates) = viewModelScope.safeLaunch {
        showProgress()
        try {
            val response = weatherRepository.getWeatherByCoordinates(coordinates)
            getWeatherForecastByCoordinates(coordinates)
            setState(response)
            weatherRepository.setCurrentCity(response.cityName)
        } finally {
            hideProgress()
        }
    }

    private fun getWeatherForecastByCity(city: City) = viewModelScope.safeLaunch {
        showProgress()
        try {
            val response = weatherRepository.getWeatherForecastByCity(city)
            _forecastState.value = response
        } finally {
            hideProgress()
        }
    }

    private fun getWeatherForecastByCoordinates(coordinates: Coordinates) =
        viewModelScope.safeLaunch {
            showProgress()
            try {
                val response = weatherRepository.getWeatherForecastByCoordinates(coordinates)
                _forecastState.value = response
            } finally {
                hideProgress()
            }
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