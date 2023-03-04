package com.example.weather_api.core_network.weather

import com.example.weather_api.app.model.Const
import com.example.weather_api.core_data.mappers.toAirPollutionEntity
import com.example.weather_api.core_data.mappers.toForecastList
import com.example.weather_api.core_data.mappers.toWeather
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_network.base.BaseRetrofitSource
import com.example.weather_api.core_network.base.RetrofitConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitWeatherSource @Inject constructor(
    config: RetrofitConfig
) : BaseRetrofitSource(config), WeatherSource {

    private val weatherApi = retrofit.create(WeatherApi::class.java)

    override suspend fun getWeatherByCity(city: String) = wrapRetrofitExceptions {
        return@wrapRetrofitExceptions weatherApi.getWeatherByCity(
            city = city,
            appId = Const.APP_ID,
            units = Const.UNITS
        ).toWeather()
    }

    override suspend fun getWeatherByCoordinates(coordinates: Coordinates) =
        wrapRetrofitExceptions {
            return@wrapRetrofitExceptions weatherApi.getWeatherByCoordinates(
                lat = coordinates.lat,
                lon = coordinates.lon,
                appId = Const.APP_ID,
                units = Const.UNITS
            ).toWeather()
        }

    override suspend fun getForecastByCoordinates(coordinates: Coordinates) =
        wrapRetrofitExceptions {
            return@wrapRetrofitExceptions weatherApi.getForecastByCoordinate(
                lat = coordinates.lat,
                lon = coordinates.lon,
                appId = Const.APP_ID,
                units = Const.UNITS,
                cnt = Const.CNT
            ).toForecastList()
        }

    override suspend fun getForecastByCity(city: String) = wrapRetrofitExceptions {
        return@wrapRetrofitExceptions weatherApi.getForecastByCity(
            city = city,
            appId = Const.APP_ID,
            units = Const.UNITS,
            cnt = Const.CNT
        ).toForecastList()
    }

    override suspend fun getAirPollutionByCoordinates(coordinates: Coordinates) =
        wrapRetrofitExceptions {
            return@wrapRetrofitExceptions weatherApi.getAirPollutionByCoordinate(
                lat = coordinates.lat,
                lon = coordinates.lon,
                appId = Const.APP_ID
            ).toAirPollutionEntity()
        }
}