package com.example.weather_api.core_data.mappers

import com.example.weather_api.app.model.ForecastState
import com.example.weather_api.app.model.WeatherType
import com.example.weather_api.app.utils.format
import com.example.weather_api.core_data.models.ForecastEntity
import com.example.weather_api.core_db.room.entitity.ForecastDbEntity
import com.example.weather_api.core_db.room.entitity.LastForecastDbEntity
import com.example.weather_api.core_network.weather.entities.GetWeatherForecastResponseEntity
import java.sql.Date
import kotlin.math.roundToInt

fun LastForecastDbEntity.toForecastEntity(): ForecastEntity = ForecastEntity(
    city = city,
    temperature = temperature,
    icon = icon,
    data = Date(data)
)

fun ForecastEntity.toLastForecastDb(): LastForecastDbEntity = LastForecastDbEntity(
    city = city,
    temperature = temperature,
    icon = icon,
    data = data.time,
)

fun ForecastDbEntity.toForecastEntity(): ForecastEntity = ForecastEntity(
    city = city,
    temperature = temperature,
    icon = icon,
    data = Date(data)
)

fun ForecastEntity.toForecastDb(): ForecastDbEntity = ForecastDbEntity(
    city = city,
    temperature = temperature,
    icon = icon,
    data = data.time,
)

fun GetWeatherForecastResponseEntity.toForecastList(): List<ForecastEntity> {
    val forecastList = mutableListOf<ForecastEntity>()

    list.map {
        forecastList.add(
            ForecastEntity(
                city = city.name,
                temperature = it.main.temp,
                icon = "ic_${it.weather.firstOrNull()?.icon}",
                data = Date((it.dt + city.timezone) * 1000),
            )
        )
    }
    return forecastList
}

fun ForecastEntity.toForecastState(dataFormat: String) = ForecastState(
    temperature = "${temperature.roundToInt()}°C",
    data = data.time.format(dataFormat),
    weatherType = WeatherType.find(icon),
)