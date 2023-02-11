package com.example.weather_api.core_data.mappers

import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.Location
import com.example.weather_api.core_data.models.WeatherEntity
import com.example.weather_api.core_network.weather.entities.GetWeatherForecastResponseEntity
import com.example.weather_api.core_network.weather.entities.GetWeatherResponseEntity
import java.sql.Date

fun GetWeatherResponseEntity.toWeather(): WeatherEntity = WeatherEntity(
    cityName = name,
    country = sys.country,
    temperature = main.temp,
    mainWeather = weather.firstOrNull()?.main ?: "error",
    description = weather.firstOrNull()?.description ?: "error",
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    pressure = main.pressure,
    windSpeed = wind.speed,
    data = Date((dt + timezone) * 1000),
    location = Location(
        city = name,
        Coordinates(
            lon = coord.lon.toString(),
            lat = coord.lat.toString()
        )
    )
)

fun GetWeatherForecastResponseEntity.toWeatherList(): List<WeatherEntity> {
    val weatherEntityList = mutableListOf<WeatherEntity>()

    list.map {
        weatherEntityList.add(
            WeatherEntity(
                cityName = city.name,
                country = city.country,
                temperature = it.main.temp,
                mainWeather = it.weather.firstOrNull()?.main ?: "error",
                description = it.weather.firstOrNull()?.description ?: "error",
                feelsLike = it.main.feelsLike,
                humidity = it.main.humidity,
                pressure = it.main.pressure,
                windSpeed = it.wind.speed,
                data = Date((it.dt + city.timezone) * 1000),
                location = Location(
                    city = city.name,
                    Coordinates(
                        lon = city.coord.lon.toString(),
                        lat = city.coord.lat.toString()
                    )
                )
            )
        )
    }
    return weatherEntityList
}