package com.example.weather_api.source.weather.entities

import com.example.weather_api.app.model.main.entities.WeatherEntity
import com.squareup.moshi.Json
import java.sql.Date

data class GetWeatherResponseEntity(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Long,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Long,
    val id: Long,
    val name: String,
    val cod: Long
) {

    data class Clouds(
        val all: Long
    )

    data class Coord(
        val lon: Double,
        val lat: Double
    )

    data class Main(
        @field:Json(name = "temp")
        val temp: Double,

        @field:Json(name = "feels_like")
        val feelsLike: Double,

        @field:Json(name = "temp_min")
        val tempMin: Double,

        @field:Json(name = "temp_max")
        val tempMax: Double,

        @field:Json(name = "pressure")
        val pressure: Long,

        @field:Json(name = "humidity")
        val humidity: Long,

        @field:Json(name = "sea_level")
        val seaLevel: Long,

        @field:Json(name = "grnd_level")
        val grndLevel: Long
    )

    data class Sys(
        val type: Long,
        val id: Long,
        val country: String,
        val sunrise: Long,
        val sunset: Long
    )

    data class Weather(
        val id: Long,
        val main: String,
        val description: String,
        val icon: String
    )

    data class Wind(
        val speed: Double,
        val deg: Long,
        val gust: Double
    )

    fun toWeather(): WeatherEntity = WeatherEntity(
        cityName = name,
        country = sys.country,
        temperature = main.temp,
        mainWeather = weather.firstOrNull()?.main ?: "error",
        description = weather.firstOrNull()?.description ?: "error",
        feelsLike = main.feelsLike,
        humidity = main.humidity,
        pressure = main.pressure,
        windSpeed = wind.speed,
        data = Date((dt + timezone) * 1000)
    )
}
