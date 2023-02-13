package com.example.weather_api.core_data

import com.example.weather_api.app.model.Const
import com.example.weather_api.core_data.mappers.toLastDB
import com.example.weather_api.core_data.mappers.toLocationDB
import com.example.weather_api.core_data.mappers.toLocation
import com.example.weather_api.core_data.models.*
import com.example.weather_api.core_db.room.AppDatabase
import com.example.weather_api.core_network.weather.WeatherSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherSource: WeatherSource,
    private val appDatabase: AppDatabase
) : WeatherRepository {

    private val currentWeatherState = MutableSharedFlow<WeatherEntity>(1)
    private val currentForecastState = MutableSharedFlow<List<WeatherEntity>>(1)
    private val currentAirPollutionState = MutableSharedFlow<AirPollutionEntity>(1)

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

    override suspend fun listenCurrentFavoritesLocations(): Flow<List<Location>> {
        return appDatabase.locationDao().getAllLocationsFlow().map { list ->
            list.map {
                it?.toLocation() ?: throw EmptyFavoritesException()
            }
        }
    }

    override suspend fun getWeatherByCity(city: City) = wrapBackendExceptions {
        delay(500)
        val response = checkForFavorites(weatherSource.getWeatherByCity(city))
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
        delay(500)
        val response = checkForFavorites(weatherSource.getWeatherByCoordinates(coordinates))
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

    override suspend fun addToFavorites() = wrapSQLiteException(Dispatchers.IO) {
        val state = currentWeatherState.replayCache[0]
        state.location.isFavorite = true
        currentWeatherState.emit(state)
        appDatabase.locationDao().insertLocation(state.location.toLocationDB())
    }

    override suspend fun deleteFromFavorites() = wrapSQLiteException(Dispatchers.IO) {
        val state = currentWeatherState.replayCache[0]
        state.location.isFavorite = false
        currentWeatherState.emit(state)
        appDatabase.locationDao().deleteLocation(state.location.city)
    }

    override suspend fun deleteFromFavoritesByCity(citiName: String) =
        wrapSQLiteException(Dispatchers.IO) {
            val state = currentWeatherState.replayCache[0]
            state.location.isFavorite = false
            currentWeatherState.emit(state)
            appDatabase.locationDao().deleteLocation(citiName)
        }

    override suspend fun getFavoriteWeatherByCoordinates(coordinates: Coordinates): WeatherEntity =
        wrapBackendExceptions {
            return weatherSource.getWeatherByCoordinates(coordinates)
        }

    override suspend fun setCurrentLocation(location: Location) {
        appDatabase.lastLocationDao().insertLastLocation(location.toLastDB())
    }

    private suspend fun checkForFavorites(response: WeatherEntity): WeatherEntity =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.locationDao().getAllLocations().forEach { locationDb ->
                if (locationDb?.city == response.cityName) {
                    response.location.isFavorite = true
                }
            }
            return@wrapSQLiteException response
        }

    private suspend fun getCurrentLocation(): Location {
        return appDatabase.lastLocationDao().getLastLocations(Const.LAST_LOCATION_KEY)?.toLocation()
            ?: Location(Const.DEFAULT_CITY, Coordinates(Const.DEFAULT_LON, Const.DEFAULT_LAT))
    }
}
