package com.example.weather_api.core_data

import com.example.weather_api.core_db.shared_preferebces.AppSettings
import com.example.weather_api.core_data.models.*
import com.example.weather_api.core_network.weather.WeatherSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherSource: WeatherSource,
    private val appSettings: AppSettings
) : WeatherRepository {

    private val currentWeatherState = MutableSharedFlow<WeatherEntity>(1)
    private val currentForecastState = MutableSharedFlow<List<WeatherEntity>>(1)
    private val currentAirPollutionState = MutableSharedFlow<AirPollutionEntity>(1)

    private fun getCurrentLocation(): Coordinates = appSettings.getCurrentCoordinates()

    private fun setCurrentLocation(location: Coordinates) {
        appSettings.setCurrentCoordinates(location)
    }

    override suspend fun listenCurrentWeatherState(): Flow<WeatherEntity> {
        getWeatherByCoordinates(getCurrentLocation())
        return currentWeatherState
    }

    override suspend fun listenCurrentForecastState(): Flow<List<WeatherEntity>> {
        getForecastByCoordinates(getCurrentLocation())
        return currentForecastState
    }

    override suspend fun listenCurrentAirPollutionState(): Flow<AirPollutionEntity> {
        getAirPollutionByCoordinate(getCurrentLocation())
        return currentAirPollutionState
    }

    override suspend fun getWeatherByCity(city: City) = wrapBackendExceptions {
        val response = weatherSource.getWeatherByCity(city)
        currentWeatherState.emit(response)
        setCurrentLocation(response.coordinates)
    }

    override suspend fun getForecastByCity(city: City) = wrapBackendExceptions {
        currentForecastState.emit(weatherSource.getWeatherForecastByCity(city))
    }

    override suspend fun getAirPollutionByCity(city: City) = wrapBackendExceptions {
        val coordinates = weatherSource.getWeatherByCity(city).coordinates
        getAirPollutionByCoordinate(coordinates)
    }

    override suspend fun getWeatherByCoordinates(coordinates: Coordinates) = wrapBackendExceptions {
        val response = weatherSource.getWeatherByCoordinates(coordinates)
        currentWeatherState.emit(response)
        setCurrentLocation(response.coordinates)
    }

    override suspend fun getForecastByCoordinates(coordinates: Coordinates) = wrapBackendExceptions {
            currentForecastState.emit(weatherSource.getWeatherForecastByCoordinates(coordinates))
        }

    override suspend fun getAirPollutionByCoordinate(coordinates: Coordinates) =
        wrapBackendExceptions {
            currentAirPollutionState.emit(weatherSource.getAirPollutionByCoordinates(coordinates))
        }
}