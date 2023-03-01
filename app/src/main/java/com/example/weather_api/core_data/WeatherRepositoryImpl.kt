package com.example.weather_api.core_data

import com.example.weather_api.app.model.Const
import com.example.weather_api.core_data.mappers.*
import com.example.weather_api.core_data.models.*
import com.example.weather_api.core_db.room.AppDatabase
import com.example.weather_api.core_db.room.entitity.WeatherUpdateFavoritesTuple
import com.example.weather_api.core_network.weather.WeatherSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherSource: WeatherSource,
    private val appDatabase: AppDatabase
) : WeatherRepository {

    override suspend fun listenCurrentWeatherState(): Flow<WeatherEntity> {
        appDatabase.forecastDao()
        return appDatabase.lastWeatherDao().getLastWeatherFlow(Const.LAST_WEATHER_KEY)
            .map { it.toWeather() }
    }

    override suspend fun listenCurrentForecastState(): Flow<List<ForecastEntity>> {
        return appDatabase.lastForecastDao().getLastForecastFlow()
            .map { it -> it.map { it.toForecastEntity() } }
    }

    override suspend fun listenCurrentAirPollutionState(): Flow<AirEntity> {
        return appDatabase.lastAirDao().getLastAirFlow(Const.LAST_WEATHER_KEY)
            .map { it.toAirEntity() }
    }

    override suspend fun listenCurrentFavoritesLocations(): Flow<List<WeatherEntity>> {
        return appDatabase.favoritesDao().getFavoritesFlow().map { list ->
            list.map {
                it.toWeather()
            }
        }
    }

    override suspend fun getWeatherByCity(city: City) = wrapBackendExceptions {
        setCurrentWeather(checkForFavorites(weatherSource.getWeatherByCity(city)))
    }

    override suspend fun getForecastByCity(city: City) = wrapBackendExceptions {
        setCurrentForecast(weatherSource.getForecastByCity(city))
    }

    override suspend fun getAirPollutionByCity(city: City) = wrapBackendExceptions {
        val coordinates = Coordinates(
            lon = weatherSource.getWeatherByCity(city).lon,
            lat = weatherSource.getWeatherByCity(city).lat
        )
        getAirPollutionByCoordinate(coordinates)
    }

    override suspend fun getWeatherByCoordinates(coordinates: Coordinates) = wrapBackendExceptions {
        setCurrentWeather(checkForFavorites(weatherSource.getWeatherByCoordinates(coordinates)))
    }

    override suspend fun getForecastByCoordinates(coordinates: Coordinates) =
        wrapBackendExceptions {
            setCurrentForecast(weatherSource.getForecastByCoordinates(coordinates))
        }

    override suspend fun getAirPollutionByCoordinate(coordinates: Coordinates) =
        wrapBackendExceptions {
            setCurrentAir(weatherSource.getAirPollutionByCoordinates(coordinates))
        }

    override suspend fun addToFavorites() = wrapSQLiteException(Dispatchers.IO) {
        val weather = appDatabase.lastWeatherDao().getLastWeather(Const.LAST_WEATHER_KEY)
        appDatabase.favoritesDao().insertFavorites(weather.toWeather().toFavoritesDbEntity())
        updateCurrentWeatherFavorites(true)
    }

    override suspend fun deleteFromFavorites() = wrapSQLiteException(Dispatchers.IO) {
        val city = appDatabase.lastWeatherDao().findByEmail(Const.LAST_WEATHER_KEY)
        appDatabase.favoritesDao().deleteFavorites(city)
        updateCurrentWeatherFavorites(false)
    }

    override suspend fun deleteFromFavoritesByCity(city: String) =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.favoritesDao().deleteFavorites(city)
            updateCurrentWeatherFavorites(false)
        }

    override suspend fun getFavoriteWeatherByCoordinates(coordinates: Coordinates): WeatherEntity =
        wrapBackendExceptions {
            withContext(Dispatchers.IO) {
                return@withContext weatherSource.getWeatherByCoordinates(coordinates)
            }
        }

    override suspend fun setCurrentWeather(city: String) = wrapSQLiteException(Dispatchers.IO){
        val weather = appDatabase.favoritesDao().getFavorites(city)
        appDatabase.lastWeatherDao().insertLastWeather(weather.toWeather().toLastWeatherDbEntity())
        val air = appDatabase.airDao().getAir(city)
        appDatabase.lastAirDao().insertLastAir(air.toAirEntity().toLastAirDb())
        val forecast = appDatabase.forecastDao().getForecast(city)
        appDatabase.lastForecastDao().insertLastForecast(forecast.map {
            it.toForecastEntity().toLastForecastDb()
        })
    }

    private suspend fun checkForFavorites(response: WeatherEntity): WeatherEntity =
        wrapSQLiteException(Dispatchers.IO) {
            response.isFavorite = appDatabase.favoritesDao().checkForFavorites(response.city)
            return@wrapSQLiteException response
        }

    private suspend fun setCurrentWeather(weather: WeatherEntity) =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.lastWeatherDao().insertLastWeather(weather.toLastWeatherDbEntity())
        }

    private suspend fun setCurrentForecast(forecast: List<ForecastEntity>) =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.lastForecastDao().deleteAll()
            appDatabase.lastForecastDao().insertLastForecast(forecast.map { it.toLastForecastDb() })
        }

    private suspend fun setCurrentAir(air: AirEntity) =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.lastAirDao().insertLastAir(air.toLastAirDb())
        }

    private suspend fun updateCurrentWeatherFavorites(isFavorites: Boolean) =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.lastWeatherDao()
                .updateFavorites(WeatherUpdateFavoritesTuple(Const.LAST_WEATHER_KEY, isFavorites))
        }
}
