package com.example.weather_api.app.di

import com.example.weather_api.app.utils.logger.LogCatLogger
import com.example.weather_api.app.utils.logger.Logger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class StuffsModule {

    @Provides
    fun providesLogger(): Logger = LogCatLogger
}