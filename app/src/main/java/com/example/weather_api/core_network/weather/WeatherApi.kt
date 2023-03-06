package com.example.weather_api.core_network.weather

import com.example.weather_api.core_network.weather.entities.GetAirRepositoryEntity
import com.example.weather_api.core_network.weather.entities.GetForecastResponseEntity
import com.example.weather_api.core_network.weather.entities.GetWeatherResponseEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather?")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") appId: String,
        @Query("units") units: String
    ): GetWeatherResponseEntity

    @GET("weather?")
    suspend fun getWeatherByCoordinates(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
        @Query("units") units: String
    ): GetWeatherResponseEntity

    @GET("forecast?")
    suspend fun getForecastByCoordinate(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
        @Query("units") units: String,
        @Query("cnt") cnt: String
    ): GetForecastResponseEntity

    @GET("forecast?")
    suspend fun getForecastByCity(
        @Query("q") city: String,
        @Query("appid") appId: String,
        @Query("units") units: String,
        @Query("cnt") cnt: String
    ): GetForecastResponseEntity

    @GET("air_pollution?")
    suspend fun getAirPollutionByCoordinate(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
    ): GetAirRepositoryEntity
}