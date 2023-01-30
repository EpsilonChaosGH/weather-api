package com.example.weather_api.core_network.weather.entities

import com.example.weather_api.core_data.models.Coordinates
import com.example.weather_api.core_data.models.WeatherEntity
import com.squareup.moshi.Json
import java.sql.Date

data class GetWeatherForecastResponseEntity(
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<ListElement>,
    val city: City
) {

    data class City(
        val id: Long,
        val name: String,
        val coord: Coord,
        val country: String,
        val population: Long,
        val timezone: Long,
        val sunrise: Long,
        val sunset: Long
    )

    data class Coord(
        val lat: Double,
        val lon: Double
    )

    data class ListElement(
        val dt: Long,
        val main: MainClass,
        val weather: List<Weather>,
        val clouds: Clouds,
        val wind: Wind,
        val visibility: Long,
        val pop: Double,
        val sys: Sys,

        @field:Json(name = "dt_txt")
        val dtTxt: String,

        val snow: Snow? = null
    )

    data class Clouds(
        val all: Long
    )

    data class MainClass(
        val temp: Double,

        @field:Json(name = "feels_like")
        val feelsLike: Double,

        @field:Json(name = "temp_min")
        val tempMin: Double,

        @field:Json(name = "temp_max")
        val tempMax: Double,

        val pressure: Long,

        @field:Json(name = "sea_level")
        val seaLevel: Long,

        @field:Json(name = "grnd_level")
        val grndLevel: Long,

        val humidity: Long,

        @field:Json(name = "temp_kf")
        val tempKf: Double
    )

    data class Snow(
        @field:Json(name = "3h")
        val the3H: Double
    )

    data class Sys(
        val pod: String
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

    fun toWeatherList(): List<WeatherEntity> {
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
                    coordinates = Coordinates(
                        lon = city.coord.lon.toString(),
                        lat = city.coord.lat.toString()
                    )
                )
            )
        }
        return weatherEntityList
    }
}