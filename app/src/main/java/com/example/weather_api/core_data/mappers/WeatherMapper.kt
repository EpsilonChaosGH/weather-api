package com.example.weather_api.core_data.mappers

import com.example.weather_api.app.model.WeatherState
import com.example.weather_api.app.model.WeatherType
import com.example.weather_api.app.utils.diffFormat
import com.example.weather_api.core_data.models.MainWeatherEntity
import com.example.weather_api.core_data.models.WeatherEntity
import com.example.weather_api.core_db.room.entitity.WeatherDbEntity
import com.example.weather_api.core_db.room.entitity.WeatherWithForecast
import com.example.weather_api.core_network.weather.entities.GetWeatherResponseEntity
import java.util.*
import kotlin.math.roundToInt

fun WeatherWithForecast.toMainWeatherEntity(): MainWeatherEntity = MainWeatherEntity(
    city = weather.weatherCity,
    isCurrent = weather.isCurrent,
    isFavorites = weather.isFavorites,
    weatherEntity = weather.weather.toWeatherEntity(weather.weatherCity),
    airEntity = weather.air.toAirEntity(),
    forecastEntityList = forecastList.map { it.toForecastEntity() }
)

fun WeatherEntity.toWeatherDb(): WeatherDbEntity = WeatherDbEntity(
    country = country,
    temperature = temperature,
    icon = icon,
    description = description,
    feelsLike = feelsLike,
    humidity = humidity,
    pressure = pressure,
    windSpeed = windSpeed,
    data = data,
    timezone = timezone,
    lon = lon,
    lat = lat
)

fun WeatherDbEntity.toWeatherEntity(city: String): WeatherEntity = WeatherEntity(
    city = city,
    country = country,
    temperature = temperature,
    icon = icon,
    description = description,
    feelsLike = feelsLike,
    humidity = humidity,
    pressure = pressure,
    windSpeed = windSpeed,
    data = data,
    timezone = timezone,
    lon = lon,
    lat = lat
)

fun WeatherEntity.toWeatherState(isFavorites: Boolean) = WeatherState(
    city = city,
    country = country,
    temperature = "${temperature.roundToInt()}°C",
    weatherType = WeatherType.find(icon),
    description = description,
    feelsLike = "${feelsLike.roundToInt()}°",
    humidity = "$humidity %",
    pressure = "$pressure hPa",
    windSpeed = "${windSpeed.roundToInt()} m/s",
    data = data.diffFormat(),
    isFavorites = isFavorites
)

fun GetWeatherResponseEntity.toWeatherEntity(): WeatherEntity = WeatherEntity(
    city = name,
    country = sys.country,
    temperature = main.temp,
    icon = "ic_${weather.firstOrNull()?.icon}",
    description = weather.firstOrNull()?.description ?: "unknown",
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    pressure = main.pressure,
    windSpeed = wind.speed,
    data = Calendar.getInstance().time.time,
    timezone = timezone,
    lon = coord.lon.toString(),
    lat = coord.lat.toString(),
)