package com.example.weather_api.core_data

import com.example.weather_api.core_data.mappers.toDB
import com.example.weather_api.core_data.mappers.toLocation
import com.example.weather_api.core_db.shared_preferebces.AppSettings
import com.example.weather_api.core_data.models.*
import com.example.weather_api.core_db.room.AppDatabase
import com.example.weather_api.core_network.weather.WeatherSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherSource: WeatherSource,
    private val appDatabase: AppDatabase,
    private val appSettings: AppSettings
) : WeatherRepository {

    private val currentWeatherState = MutableSharedFlow<WeatherEntity>(1)
    private val currentForecastState = MutableSharedFlow<List<WeatherEntity>>(1)
    private val currentAirPollutionState = MutableSharedFlow<AirPollutionEntity>(1)
    private val currentFavoritesLocations = MutableSharedFlow<List<WeatherEntity>>(1)

    private fun getCurrentLocation(): Location = appSettings.getCurrentLocation()

    private fun setCurrentLocation(location: Location) {
        appSettings.setCurrentLocation(location)
    }

    override suspend fun listenCurrentWeatherState(): Flow<WeatherEntity> {
        getWeatherByCoordinates(getCurrentLocation().coordinates)
        return currentWeatherState
    }

    override suspend fun listenCurrentForecastState(): Flow<List<WeatherEntity>> {
        getForecastByCoordinates(getCurrentLocation().coordinates)
        return currentForecastState
    }

    override suspend fun listenCurrentAirPollutionState(): Flow<AirPollutionEntity> {
        getAirPollutionByCoordinate(getCurrentLocation().coordinates)
        return currentAirPollutionState
    }

    override suspend fun listenCurrentFavoritesLocations(): Flow<List<WeatherEntity>> {
        loadFavoritesLocations()
        return currentFavoritesLocations
    }

    override suspend fun getWeatherByCity(city: City) = wrapBackendExceptions {
        delay(1000)
        val response = weatherSource.getWeatherByCity(city)
        appDatabase.locationDao().getAll().map { locationDB -> locationDB.toLocation() }
            .forEach { location ->
                if (location.city == response.cityName) {
                    response.location.isFavorite = true
                }
            }
        currentWeatherState.emit(response)
        setCurrentLocation(response.location)
    }

    override suspend fun getForecastByCity(city: City) = wrapBackendExceptions {
        currentForecastState.emit(weatherSource.getWeatherForecastByCity(city))
    }

    override suspend fun getAirPollutionByCity(city: City) = wrapBackendExceptions {
        val coordinates = weatherSource.getWeatherByCity(city).location.coordinates
        getAirPollutionByCoordinate(coordinates)
    }

    override suspend fun getWeatherByCoordinates(coordinates: Coordinates) = wrapBackendExceptions {
        delay(1000)
        val response = weatherSource.getWeatherByCoordinates(coordinates)
        appDatabase.locationDao().getAll().map { locationDB -> locationDB.toLocation() }
            .forEach { location ->
                if (location.city == response.cityName) {
                    response.location.isFavorite = true
                }
            }
        currentWeatherState.emit(response)
        setCurrentLocation(response.location)
    }

    override suspend fun getForecastByCoordinates(coordinates: Coordinates) =
        wrapBackendExceptions {
            currentForecastState.emit(weatherSource.getWeatherForecastByCoordinates(coordinates))
        }

    override suspend fun getAirPollutionByCoordinate(coordinates: Coordinates) =
        wrapBackendExceptions {
            currentAirPollutionState.emit(weatherSource.getAirPollutionByCoordinates(coordinates))
        }

    override suspend fun addToFavorites() {
        val state = currentWeatherState.replayCache[0]
        state.location.isFavorite = true
        currentWeatherState.emit(state)
        appDatabase.locationDao().insertAll(state.location.toDB())
        loadFavoritesLocations()
    }

    override suspend fun removeFromFavorites() {
        val state = currentWeatherState.replayCache[0]
        state.location.isFavorite = false
        currentWeatherState.emit(state)
        appDatabase.locationDao().delete(state.cityName)
        loadFavoritesLocations()

    }

    private suspend fun loadFavoritesLocations() {
        val favoritesLocationList = mutableListOf<WeatherEntity>()
        appDatabase.locationDao().getAll().map { locationDB ->
            coroutineScope {
                async {
                    wrapBackendExceptions {
                        val response =
                            weatherSource.getWeatherByCoordinates(locationDB.toLocation().coordinates)
                        favoritesLocationList.add(response)
                    }
                    currentFavoritesLocations.emit(favoritesLocationList)
                }
            }
        }
    }
}