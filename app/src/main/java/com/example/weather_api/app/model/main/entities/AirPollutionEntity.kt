package com.example.weather_api.app.model.main.entities

import com.example.weather_api.app.model.AirQuality

data class AirPollutionEntity(
    var co: Double = 0.0,
    var no: Double = 0.0,
    var no2: Double = 0.0,
    var no2Quality: AirQuality,
    var o3: Double = 0.0,
    var o3Quality: AirQuality,
    var so2: Double = 0.0,
    var pm2_5: Double = 0.0,
    var pm2_5Quality: AirQuality,
    var pm10: Double = 0.0,
    var pm10Quality: AirQuality,
    var nh3: Double = 0.0
)
