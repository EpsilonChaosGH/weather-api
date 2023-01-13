package com.example.weather_api.source.weather

import com.example.weather_api.app.Const
import com.example.weather_api.app.model.main.WeatherSource
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.source.base.BaseRetrofitSource
import com.example.weather_api.source.base.RetrofitConfig
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RetrofitWeatherSource @Inject constructor(
    config: RetrofitConfig
) : BaseRetrofitSource(config), WeatherSource {

    private val weatherApi = retrofit.create(WeatherApi::class.java)

    override suspend fun getWeatherByCity(city: City) = wrapRetrofitExceptions {
        return@wrapRetrofitExceptions weatherApi.getWeatherByCity(
            city = city.cityName,
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

    override suspend fun getWeatherForecastByCoordinates(coordinates: Coordinates) =
        wrapRetrofitExceptions {
            return@wrapRetrofitExceptions weatherApi.getWeatherForecastByCoordinate(
                lat = coordinates.lat,
                lon = coordinates.lon,
                appId = Const.APP_ID,
                units = Const.UNITS,
                cnt = Const.CNT
            ).toWeatherList()
        }

    override suspend fun getWeatherForecastByCity(city: City) = wrapRetrofitExceptions {
        return@wrapRetrofitExceptions weatherApi.getWeatherForecastByCity(
            city = city.cityName,
            appId = Const.APP_ID,
            units = Const.UNITS,
            cnt = Const.CNT
        ).toWeatherList()
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