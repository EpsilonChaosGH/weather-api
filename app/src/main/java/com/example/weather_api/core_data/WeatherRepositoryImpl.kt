package com.example.weather_api.core_data

import com.example.weather_api.core_data.mappers.*
import com.example.weather_api.core_data.models.*
import com.example.weather_api.core_db.room.AppDatabase
import com.example.weather_api.core_db.room.entitity.*
import com.example.weather_api.core_network.weather.WeatherSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherSource: WeatherSource,
    private val appDatabase: AppDatabase,
    private val ioDispatcher: CoroutineDispatcher
) {

    fun listenMainWeather(): Flow<MainWeatherEntity?> {
        return appDatabase.weatherDao().getCurrentWeatherFlow()
            .map { it?.toMainWeatherEntity() }
    }

    fun listenFavoriteLocations(): Flow<List<MainWeatherEntity?>> {
        return appDatabase.weatherDao().getFavoritesFlow()
            .map { list ->
                list.map {
                    it?.toMainWeatherEntity()
                }
            }
    }

    suspend fun getMainWeatherByCity(city: String) = wrapBackendExceptions {
        delay(1000)
        val weather = weatherSource.getWeatherByCity(city)
        val coordinates = Coordinates(lon = weather.lon, lat = weather.lat)
        val air = weatherSource.getAirPollutionByCoordinates(coordinates)
        val forecast = weatherSource.getForecastByCity(city)

        setCurrentWeather(weather = weather, air = air, forecast = forecast)
    }

    suspend fun getMainWeatherByCoordinates(coordinates: Coordinates) =
        wrapBackendExceptions {
            delay(1000)
            val weather = weatherSource.getWeatherByCoordinates(coordinates)
            val air = weatherSource.getAirPollutionByCoordinates(coordinates)
            val forecast = weatherSource.getForecastByCoordinates(coordinates)

            setCurrentWeather(weather = weather, air = air, forecast = forecast)
        }

    suspend fun refreshCurrentMainWeather() = wrapSQLiteException(ioDispatcher) {
        getMainWeatherByCity(appDatabase.weatherDao().getCurrentCity())
    }

    suspend fun addToFavoritesByCity(city: String) = wrapSQLiteException(ioDispatcher) {
        appDatabase.weatherDao()
            .updateFavorites(UpdateFavoritesTuple(city = city, isFavorites = true))
    }

    suspend fun deleteFromFavoritesByCity(city: String) =
        wrapSQLiteException(ioDispatcher) {
            if (appDatabase.weatherDao().getCurrentCity() == city) {
                appDatabase.weatherDao().updateFavorites(
                    UpdateFavoritesTuple(city = city, false)
                )
            } else {
                appDatabase.weatherDao().deleteMainWeatherByCity(city)
            }
        }

    suspend fun refreshFavorites() = wrapSQLiteException(ioDispatcher) {
        val favoritesList = appDatabase.weatherDao().getFavoritesCity()
        val favoritesDef = mutableListOf<Deferred<MainWeatherEntity>>()
        favoritesList.map {
            val response = async {
                return@async getMainWeatherByCityForFavorites(city = it.city)
            }
            favoritesDef.add(response)
        }
        val favorites = favoritesDef.map { it.await() }.toMutableList()
        favorites.sortBy { it.city }
        favorites.forEach { updateFavorites(it) }
    }

    private suspend fun updateFavorites(weather: MainWeatherEntity) =
        wrapSQLiteException(ioDispatcher) {
            appDatabase.weatherDao().updateMainWeather(
                UpdateMainWeatherTuple(
                    weatherCity = weather.city,
                    weather = weather.weatherEntity.toWeatherDb(),
                    air = weather.airEntity.toAirDb()
                )
            )
            appDatabase.weatherDao()
                .insertForecast(weather.forecastEntityList.map { it.toForecastDbEntity() })
        }

    private suspend fun getMainWeatherByCityForFavorites(city: String): MainWeatherEntity =
        wrapBackendExceptions {
            val weather = weatherSource.getWeatherByCity(city)
            val coordinates = Coordinates(lon = weather.lon, lat = weather.lat)
            val air = weatherSource.getAirPollutionByCoordinates(coordinates)
            val forecast = weatherSource.getForecastByCity(city)
            return MainWeatherEntity(
                city = city,
                isCurrent = false,
                isFavorites = true,
                weatherEntity = weather,
                airEntity = air,
                forecastEntityList = forecast
            )
        }

    suspend fun fromFavoritesMainWeatherToCurrent(city: String) =
        wrapSQLiteException(ioDispatcher) {
            appDatabase.weatherDao().setCurrentByCity(city)
        }

    private suspend fun setCurrentWeather(
        weather: WeatherEntity, air: AirEntity, forecast: List<ForecastEntity>
    ) = wrapSQLiteException(ioDispatcher) {
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
        appDatabase.weatherDao().setCurrentByCity(weather.city)
    }
}