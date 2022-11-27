package com.example.weather_api.source.base

import com.example.weather_api.app.model.SourcesProvider
import com.example.weather_api.app.model.main.WeatherSource
import com.example.weather_api.source.weather.RetrofitWeatherSource

class RetrofitSourcesProvider(
    private val config: RetrofitConfig
) : SourcesProvider {

    override fun getWeatherSource(): WeatherSource {
        return RetrofitWeatherSource(config)
    }
}