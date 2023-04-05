package com.example.weather_api.core_data.mappers

import com.example.weather_api.app.model.ForecastState
import com.example.weather_api.app.model.WeatherType
import com.example.weather_api.app.utils.format
import com.example.weather_api.core_data.models.ForecastEntity
import com.example.weather_api.core_db.room.entitity.ForecastDbEntity
import com.example.weather_api.core_network.weather.entities.GetForecastResponseEntity
import kotlin.math.roundToInt

fun ForecastDbEntity.toForecastEntity(): ForecastEntity = ForecastEntity(
    city = city,
    temperature = temperature,
    icon = icon,
    data = data
)

fun ForecastEntity.toForecastDbEntity(): ForecastDbEntity = ForecastDbEntity(
    id = 0,
    city = city,
    temperature = temperature,
    icon = icon,
    data = data
)

fun GetForecastResponseEntity.toForecastList(): List<ForecastEntity> {
    val forecastList = mutableListOf<ForecastEntity>()
    list.map {
        forecastList.add(
            ForecastEntity(
                city = city.name,
                temperature = it.main.temp,
                icon = "ic_${it.weather.firstOrNull()?.icon}",
                data = (it.dt + city.timezone) * 1000,
            )
        )
    }
    return forecastList
}

fun ForecastEntity.toForecastState(dataFormat: String, timeZone: Long) = ForecastState(
    temperature = "${temperature.roundToInt()}°C",
    data = data.format(dataFormat, timeZone),
    weatherType = WeatherType.find(icon),
)