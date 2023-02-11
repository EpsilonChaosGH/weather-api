package com.example.weather_api.core_network.weather.entities

data class GetAirPollutionRepositoryEntity(
    val coord: Coord,
    val list: List<ListElement>
) {

    data class Coord(
        val lon: Double,
        val lat: Double
    )

    data class ListElement(
        val main: Main,
        val components: Map<String, Double>,
        val dt: Long
    )

    data class Main(
        val aqi: Long
    )
}