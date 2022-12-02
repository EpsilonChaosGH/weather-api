package com.example.weather_api.app

import android.content.Context
import com.example.weather_api.app.model.SourcesProvider
import com.example.weather_api.app.model.main.WeatherRepository
import com.example.weather_api.app.model.main.WeatherSource
import com.example.weather_api.app.model.settings.AppSettings
import com.example.weather_api.app.model.settings.SharedPreferencesAppSettings
import com.example.weather_api.source.SourceProviderHolder

object Singletons {

    private lateinit var appContext: Context

    private val sourcesProvider: SourcesProvider by lazy {
        SourceProviderHolder.sourcesProvider
    }

    private val appSettings: AppSettings by lazy {
        SharedPreferencesAppSettings(appContext)
    }

    private val weatherSource: WeatherSource by lazy {
        sourcesProvider.getWeatherSource()
    }

    val weatherRepository: WeatherRepository by lazy {
        WeatherRepository(
            weatherSource,
            appSettings
        )
    }

    fun init(appContext: Context) {
        Singletons.appContext = appContext
    }
}
