package com.example.weather_api.app.di

import com.example.weather_api.app.utils.ConnectivityObserver
import com.example.weather_api.app.utils.NetworkConnectivityObserver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkStateModule {

    @Binds
    abstract fun bindConnectivityObserver(
        networkConnectivityObserver: NetworkConnectivityObserver
    ): ConnectivityObserver
}