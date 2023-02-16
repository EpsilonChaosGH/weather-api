package com.example.weather_api.core_data.mappers

import com.example.weather_api.app.model.WeatherState
import com.example.weather_api.app.model.WeatherType
import com.example.weather_api.app.utils.FORMAT_EEE_d_MMMM_HH_mm
import com.example.weather_api.app.utils.format
import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.Location
import com.example.weather_api.core_data.models.WeatherEntity
import com.example.weather_api.core_network.weather.entities.GetWeatherForecastResponseEntity
import com.example.weather_api.core_network.weather.entities.GetWeatherResponseEntity
import java.sql.Date
import kotlin.math.roundToInt

fun GetWeatherResponseEntity.toWeather(): WeatherEntity = WeatherEntity(
    cityName = name,
    country = sys.country,
    temperature = main.temp,
    icon = "ic_${weather.firstOrNull()?.icon}",
    description = weather.firstOrNull()?.description ?: "unknown",
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
                icon = "ic_${it.weather.firstOrNull()?.icon}",
                description = it.weather.firstOrNull()?.description ?: "unknown",
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

fun WeatherEntity.toWeatherState(dataFormat: String) = WeatherState(
    cityName = cityName,
    country = country,
    temperature = "${temperature.roundToInt()}°C",
    weatherType = WeatherType.find(icon),
    description = description,
    feelsLike = "${feelsLike.roundToInt()}°",
    humidity = "$humidity %",
    pressure = "$pressure hPa",
    windSpeed = "${windSpeed.roundToInt()} m/s",
    data = data.time.format(dataFormat),
    location = location
)