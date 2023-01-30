package com.example.weather_api.app.di

import com.example.weather_api.core_db.shared_preferebces.AppSettings
import com.example.weather_api.core_db.shared_preferebces.SharedPreferencesAppSettings
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SettingsModule {

    @Binds
    abstract fun bindAppSettings(
        appSettings: SharedPreferencesAppSettings
    ): AppSettings
}