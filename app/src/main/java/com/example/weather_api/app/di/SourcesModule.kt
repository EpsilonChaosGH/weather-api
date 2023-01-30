package com.example.weather_api.app.di

import com.example.weather_api.core_network.weather.WeatherSource
import com.example.weather_api.core_network.weather.RetrofitWeatherSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourcesModule {

    @Binds
    abstract fun bindWeatherSources(
        retrofitWeatherSource: RetrofitWeatherSource
    ): WeatherSource
}