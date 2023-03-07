package com.example.weather_api.core_data

import com.example.weather_api.core_data.mappers.*
import com.example.weather_api.core_data.models.*
import com.example.weather_api.core_db.room.AppDatabase
import com.example.weather_api.core_db.room.entitity.*
import com.example.weather_api.core_network.weather.WeatherSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherSource: WeatherSource,
    private val appDatabase: AppDatabase,
) : WeatherRepository {

    override suspend fun listenMainWeather(): Flow<MainWeatherEntity?> {
        return appDatabase.weatherDao().getCurrentWeatherFlow(isCurrent = true)
            .map { it?.toMainWeatherEntity() }
    }

    override suspend fun listenFavoriteLocations(): Flow<List<MainWeatherEntity?>> {
        return appDatabase.weatherDao().getFavoritesFlow(isFavorites = true)
            .map { list ->
                list.map {
                    it?.toMainWeatherEntity()
                }
            }
    }

    override suspend fun getMainWeatherByCity(city: String) = wrapBackendExceptions {
        val weather = weatherSource.getWeatherByCity(city)
        val coordinates = Coordinates(lon = weather.lon, lat = weather.lat)
        val air = weatherSource.getAirPollutionByCoordinates(coordinates)
        val forecast = weatherSource.getForecastByCity(city)

        setCurrentWeather(weather = weather, air = air, forecast = forecast)
    }

    override suspend fun getMainWeatherByCoordinates(coordinates: Coordinates) =
        wrapBackendExceptions {
            val weather = weatherSource.getWeatherByCoordinates(coordinates)
            val air = weatherSource.getAirPollutionByCoordinates(coordinates)
            val forecast = weatherSource.getForecastByCoordinates(coordinates)

            setCurrentWeather(weather = weather, air = air, forecast = forecast)
        }

    override suspend fun addToFavoritesByCity(city: String) = wrapSQLiteException(Dispatchers.IO) {
        appDatabase.weatherDao().updateFavorites(
            UpdateFavoritesTuple(city = city, isFavorites = true)
        )
    }

    override suspend fun deleteFromFavoritesByCity(city: String) =
        wrapSQLiteException(Dispatchers.IO) {
            if (appDatabase.weatherDao().getCurrentCity(true) == city) {
                appDatabase.weatherDao().updateFavorites(
                    UpdateFavoritesTuple(city = city, false)
                )
            } else {
                appDatabase.weatherDao().deleteMainWeatherByCity(city)
            }
        }

    override suspend fun getFavoriteWeatherByCoordinates(coordinates: Coordinates): WeatherEntity =
        wrapBackendExceptions {
            withContext(Dispatchers.IO) {
                return@withContext weatherSource.getWeatherByCoordinates(coordinates)
            }
        }

    override suspend fun fromFavoritesMainWeatherToCurrent(city: String) =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.weatherDao().updateAllCurrent(current = true, new_current = false)
            appDatabase.weatherDao()
                .updateCurrent(UpdateCurrentTuple(city = city, isCurrent = true))
        }

    private suspend fun setCurrentWeather(
        weather: WeatherEntity, air: AirEntity, forecast: List<ForecastEntity>
    ) = wrapSQLiteException(Dispatchers.IO) {
        appDatabase.weatherDao().insertWeather(
            MainWeatherDbEntity(
                weatherCity = weather.city,
                isCurrent = true,
                isFavorites = appDatabase.weatherDao().checkForFavorites(weather.city, true),
                weather = weather.toWeatherDb(),
                air = air.toAirDb()
            )
        )
        appDatabase.weatherDao().insertForecast(forecast.map { it.toForecastDbEntity() })
        appDatabase.weatherDao().updateAllCurrent(current = true, new_current = false)
        appDatabase.weatherDao()
            .updateCurrent(UpdateCurrentTuple(city = weather.city, isCurrent = true))
    }
}