package com.example.weather_api.app.di

import android.content.Context
import androidx.room.Room
import com.example.weather_api.core_db.room.AppDatabase
import com.example.weather_api.core_db.room.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "WeatherApp-DB"
        ).createFromAsset("init_db.db")
            .build()

    @Provides
    @Singleton
    fun provideFavoritesDao(db: AppDatabase): FavoritesDao = db.favoritesDao()

    @Provides
    @Singleton
    fun provideAirDao(db: AppDatabase): AirDao = db.airDao()

    @Provides
    @Singleton
    fun provideForecastDao(db: AppDatabase): ForecastDao = db.forecastDao()

    @Provides
    @Singleton
    fun provideLastWeatherDao(db: AppDatabase): LastWeatherDao = db.lastWeatherDao()

    @Provides
    @Singleton
    fun provideLastAirDao(db: AppDatabase): LastAirDao = db.lastAirDao()

    @Provides
    @Singleton
    fun provideLastForecastDao(db: AppDatabase): LastForecastDao = db.lastForecastDao()
}