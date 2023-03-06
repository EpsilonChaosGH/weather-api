package com.example.weather_api.core_network.weather.entities

data class GetAirRepositoryEntity(
    val list: List<ListElement>
) {
    data class ListElement(
        val components: Map<String, Double>,
    )
}