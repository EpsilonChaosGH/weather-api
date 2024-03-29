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
        )
            .build()

    @Provides
    @Singleton
    fun weatherDao(db: AppDatabase): WeatherDao = db.weatherDao()
}