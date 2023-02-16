package com.example.weather_api.core_data.mappers

import com.example.weather_api.app.model.AirPollutionState
import com.example.weather_api.app.model.AirQuality
import com.example.weather_api.core_data.models.AirPollutionEntity
import com.example.weather_api.core_network.weather.entities.GetAirPollutionRepositoryEntity
import kotlin.math.roundToInt

fun GetAirPollutionRepositoryEntity.toAirPollutionEntity() = AirPollutionEntity(
    no2 = list.firstOrNull()?.components?.get("no2") ?: -1.0,
    o3 = list.firstOrNull()?.components?.get("o3") ?: -1.0,
    pm10 = list.firstOrNull()?.components?.get("pm10") ?: -1.0,
    pm25 = list.firstOrNull()?.components?.get("pm2_5") ?: -1.0
)

fun AirPollutionEntity.toAirPollutionState() = AirPollutionState(
    no2 = "${no2.roundToInt()} μg/m3",
    no2Quality = checkNo2(no2),
    o3 = "${o3.roundToInt()} μg/m3",
    o3Quality = checkO3(o3),
    pm10 = "${pm10.roundToInt()} μg/m3",
    pm10Quality = checkPm10(pm10),
    pm25 = "${pm25.roundToInt()} μg/m3",
    pm25Quality = checkPm25(pm25),
)

private fun checkNo2(no2: Double): AirQuality {
    return when (no2) {
        in 0.0..50.0 -> AirQuality.GOOD
        in 50.0..100.0 -> AirQuality.FAIR
        in 100.0..200.0 -> AirQuality.MODERATE
        in 200.0..400.0 -> AirQuality.POOR
        in 400.0..1000.0 -> AirQuality.VERY_POOR
        else -> {
            AirQuality.ERROR
        }
    }
}

private fun checkPm10(no2: Double): AirQuality {
    return when (no2) {
        in 0.0..25.0 -> AirQuality.GOOD
        in 25.0..50.0 -> AirQuality.FAIR
        in 50.0..90.0 -> AirQuality.MODERATE
        in 9.0..180.0 -> AirQuality.POOR
        in 180.0..1000.0 -> AirQuality.VERY_POOR
        else -> {
            AirQuality.ERROR
        }
    }
}

private fun checkO3(no2: Double): AirQuality {
    return when (no2) {
        in 0.0..60.0 -> AirQuality.GOOD
        in 60.0..120.0 -> AirQuality.FAIR
        in 120.0..180.0 -> AirQuality.MODERATE
        in 180.0..240.0 -> AirQuality.POOR
        in 240.0..1000.0 -> AirQuality.VERY_POOR
        else -> {
            AirQuality.ERROR
        }
    }
}

private fun checkPm25(no2: Double): AirQuality {
    return when (no2) {
        in 0.0..15.0 -> AirQuality.GOOD
        in 15.0..30.0 -> AirQuality.FAIR
        in 30.0..55.0 -> AirQuality.MODERATE
        in 55.0..110.0 -> AirQuality.POOR
        in 110.0..1000.0 -> AirQuality.VERY_POOR
        else -> {
            AirQuality.ERROR
        }
    }
}