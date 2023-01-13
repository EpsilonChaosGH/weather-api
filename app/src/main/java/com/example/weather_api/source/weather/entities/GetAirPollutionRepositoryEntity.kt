package com.example.weather_api.source.weather.entities

import com.example.weather_api.app.model.AirQuality
import com.example.weather_api.app.model.main.entities.AirPollutionEntity

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

    fun toAirPollutionEntity() = AirPollutionEntity(
        co = list[0].components["co"] ?: -1.0,
        no = list[0].components["no"] ?: -1.0,
        no2 = list[0].components["no2"] ?: -1.0,
        no2Quality = checkNo2(list[0].components["no2"] ?: -1.0),
        o3 = list[0].components["o3"] ?: -1.0,
        o3Quality = checkO3(list[0].components["o3"] ?: -1.0),
        so2 = list[0].components["so2"] ?: -1.0,
        pm2_5 = list[0].components["pm2_5"] ?: -1.0,
        pm2_5Quality = checkPm25(list[0].components["pm2_5"] ?: -1.0),
        pm10 = list[0].components["pm10"] ?: -1.0,
        pm10Quality = checkPm10(list[0].components["pm10"] ?: -1.0),
        nh3 = list[0].components["nh3"] ?: -1.0,

        )

    private fun checkNo2(no2: Double): AirQuality {
        return when (no2) {
            in 0.0..50.0 -> AirQuality.Good
            in 50.0..100.0 -> AirQuality.Fair
            in 100.0..200.0 -> AirQuality.Moderate
            in 200.0..400.0 -> AirQuality.Poor
            in 400.0..1000.0 -> AirQuality.VeryPoor
            else -> {
                AirQuality.Error
            }
        }
    }

    private fun checkPm10(no2: Double): AirQuality {
        return when (no2) {
            in 0.0..25.0 -> AirQuality.Good
            in 25.0..50.0 -> AirQuality.Fair
            in 50.0..90.0 -> AirQuality.Moderate
            in 9.0..180.0 -> AirQuality.Poor
            in 180.0..1000.0 -> AirQuality.VeryPoor
            else -> {
                AirQuality.Error
            }
        }
    }

    private fun checkO3(no2: Double): AirQuality {
        return when (no2) {
            in 0.0..60.0 -> AirQuality.Good
            in 60.0..120.0 -> AirQuality.Fair
            in 120.0..180.0 -> AirQuality.Moderate
            in 180.0..240.0 -> AirQuality.Poor
            in 240.0..1000.0 -> AirQuality.VeryPoor
            else -> {
                AirQuality.Error
            }
        }
    }

    private fun checkPm25(no2: Double): AirQuality {
        return when (no2) {
            in 0.0..15.0 -> AirQuality.Good
            in 15.0..30.0 -> AirQuality.Fair
            in 30.0..55.0 -> AirQuality.Moderate
            in 55.0..110.0 -> AirQuality.Poor
            in 110.0..1000.0 -> AirQuality.VeryPoor
            else -> {
                AirQuality.Error
            }
        }
    }
}