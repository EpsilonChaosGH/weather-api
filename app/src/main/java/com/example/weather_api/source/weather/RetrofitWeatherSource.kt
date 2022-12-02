package com.example.weather_api.source.weather

import com.example.weather_api.app.Const
import com.example.weather_api.app.model.main.WeatherSource
import com.example.weather_api.app.model.main.entities.City
import com.example.weather_api.app.model.main.entities.Coordinates
import com.example.weather_api.source.base.BaseRetrofitSource
import com.example.weather_api.source.base.RetrofitConfig
import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity
import kotlinx.coroutines.delay

class RetrofitWeatherSource(
    config: RetrofitConfig
) : BaseRetrofitSource(config), WeatherSource {

    private val weatherApi = retrofit.create(WeatherApi::class.java)

    override suspend fun getWeatherByCity(city: City) = wrapRetrofitExceptions {
        delay(1000)
        return@wrapRetrofitExceptions weatherApi.getWeatherByCity(
            city = city.cityName,
            appId = Const.APP_ID,
            units = Const.UNITS,
            lang = Const.LANG
        )
    }

    override suspend fun getWeatherByCoordinates(coordinates: Coordinates) = wrapRetrofitExceptions {
        delay(1000)
        return@wrapRetrofitExceptions weatherApi.getWeatherByCoordinates(
            lat = coordinates.lat,
            lon = coordinates.lon,
            appId = Const.APP_ID,
            units = Const.UNITS,
            lang = Const.LANG
        )
    }
}
