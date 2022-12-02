package com.example.weather_api.source.weather

import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherApi {
    @POST("weather?")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") appId: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): GetWeatherResponseEntity

    @POST("weather?")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
        @Query("units") units: String,
        @Query("lang") lang: String
    ): GetWeatherResponseEntity
}