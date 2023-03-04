package com.example.weather_api.core_data.mappers

import com.example.weather_api.app.model.WeatherState
import com.example.weather_api.app.model.WeatherType
import com.example.weather_api.app.utils.format
import com.example.weather_api.core_data.models.MainWeatherEntity
import com.example.weather_api.core_data.models.WeatherEntity
import com.example.weather_api.core_db.room.entitity.WeatherDbEntity
import com.example.weather_api.core_db.room.entitity.WeatherWithForecast
import com.example.weather_api.core_network.weather.entities.GetWeatherForecastResponseEntity
import com.example.weather_api.core_network.weather.entities.GetWeatherResponseEntity
import kotlin.math.roundToInt

fun WeatherWithForecast.toMainWeatherEntity(): MainWeatherEntity = MainWeatherEntity(
    city = weather.weatherCity,
    isCurrent = weather.isCurrent,
    isFavorites = weather.isFavorites,
    weatherEntity = weather.weather.toWeatherEntity(),
    airEntity = weather.air.toAirEntity(),
    forecastEntityList = forecastList.map { it.toForecastEntity() }
)

fun WeatherEntity.toWeatherDb(): WeatherDbEntity = WeatherDbEntity(
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
    lon = lon,
    lat = lat
)

fun WeatherDbEntity.toWeatherEntity(): WeatherEntity = WeatherEntity(
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
    lon = lon,
    lat = lat
)

fun GetWeatherResponseEntity.toWeather(): WeatherEntity = WeatherEntity(
    city = name,
    country = sys.country,
    temperature = main.temp,
    icon = "ic_${weather.firstOrNull()?.icon}",
    description = weather.firstOrNull()?.description ?: "unknown",
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    pressure = main.pressure,
    windSpeed = wind.speed,
    data = dt + timezone * 1000,
    lon = coord.lon.toString(),
    lat = coord.lat.toString(),
)

fun GetWeatherForecastResponseEntity.toWeatherList(): List<WeatherEntity> {
    val weatherEntityList = mutableListOf<WeatherEntity>()

    list.map {
        weatherEntityList.add(
            WeatherEntity(
                city = city.name,
                country = city.country,
                temperature = it.main.temp,
                icon = "ic_${it.weather.firstOrNull()?.icon}",
                description = it.weather.firstOrNull()?.description ?: "unknown",
                feelsLike = it.main.feelsLike,
                humidity = it.main.humidity,
                pressure = it.main.pressure,
                windSpeed = it.wind.speed,
                data = it.dt + city.timezone * 1000,
                lon = city.coord.lon.toString(),
                lat = city.coord.lat.toString(),
            )
        )
    }
    return weatherEntityList
}

fun WeatherEntity.toWeatherState(dataFormat: String, isFavorites: Boolean) = WeatherState(
    city = city,
    country = country,
    temperature = "${temperature.roundToInt()}°C",
    weatherType = WeatherType.find(icon),
    description = description,
    feelsLike = "${feelsLike.roundToInt()}°",
    humidity = "$humidity %",
    pressure = "$pressure hPa",
    windSpeed = "${windSpeed.roundToInt()} m/s",
    data = data.format(dataFormat),
    isFavorites = isFavorites
)