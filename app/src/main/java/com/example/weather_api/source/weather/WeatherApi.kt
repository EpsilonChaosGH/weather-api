package com.example.weather_api.source.weather

import com.example.weather_api.source.weather.entities.GetAirPollutionRepositoryEntity
import com.example.weather_api.source.weather.entities.GetWeatherForecastResponseEntity
import com.example.weather_api.source.weather.entities.GetWeatherResponseEntity
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
    suspend fun getWeatherForecastByCoordinate(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
        @Query("units") units: String,
        @Query("cnt") cnt: String
    ): GetWeatherForecastResponseEntity

    @GET("forecast?")
    suspend fun getWeatherForecastByCity(
        @Query("q") city: String,
        @Query("appid") appId: String,
        @Query("units") units: String,
        @Query("cnt") cnt: String
    ): GetWeatherForecastResponseEntity

    @GET("air_pollution?")
    suspend fun getAirPollutionByCoordinate(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("appid") appId: String,
    ): GetAirPollutionRepositoryEntity
}