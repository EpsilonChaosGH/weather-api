package com.example.weather_api.core_data

import com.example.weather_api.core_data.mappers.*
import com.example.weather_api.core_data.models.*
import com.example.weather_api.core_db.room.AppDatabase
import com.example.weather_api.core_db.room.entitity.*
import com.example.weather_api.core_network.weather.WeatherSource
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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

    override suspend fun refreshCurrentMainWeather() = wrapSQLiteException(Dispatchers.IO) {
        getMainWeatherByCity(appDatabase.weatherDao().getCurrentCity(isCurrent = true))
    }

    override suspend fun addToFavoritesByCity(city: String) = wrapSQLiteException(Dispatchers.IO) {
        appDatabase.weatherDao()
            .updateFavorites(UpdateFavoritesTuple(city = city, isFavorites = true))
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

    override suspend fun refreshFavorites() = wrapSQLiteException(Dispatchers.IO) {
        val favoritesList = appDatabase.weatherDao().getFavoritesCity(isFavorites = true)

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

    private suspend fun updateFavorites(weather: MainWeatherEntity) {
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

    private suspend fun getMainWeatherByCityForFavorites(city: String): MainWeatherEntity {
        val weather = weatherSource.getWeatherByCity(city)
        val coordinates = Coordinates(lon = weather.lon, lat = weather.lat)
        val air = weatherSource.getAirPollutionByCoordinates(coordinates)
        val forecast = weatherSource.getForecastByCity(city)
        return MainWeatherEntity(
            city = weather.city,
            isCurrent = false,
            isFavorites = true,
            weatherEntity = weather,
            airEntity = air,
            forecastEntityList = forecast
        )
    }

    override suspend fun fromFavoritesMainWeatherToCurrent(city: String) =
        wrapSQLiteException(Dispatchers.IO) {
            appDatabase.weatherDao().setCurrentByCity(city)
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
        appDatabase.weatherDao().setCurrentByCity(weather.city)
    }
}