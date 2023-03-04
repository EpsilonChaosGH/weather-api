package com.example.weather_api.app.model


data class AirState(
    var no2: String,
    var no2Quality: AirQuality,
    var o3: String,
    var o3Quality: AirQuality,
    var pm10: String,
    var pm10Quality: AirQuality,
    var pm25: String,
    var pm25Quality: AirQuality
)
