package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.ForecastDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {

    @Query("SELECT * FROM forecast WHERE city = :city")
    fun getForecastFlow(city: String): Flow<List<ForecastDbEntity>>

    @Query("SELECT * FROM forecast WHERE city = :city")
    fun getForecast(city: String): List<ForecastDbEntity>

    @Insert
    suspend fun insertForecast(forecastList: List<ForecastDbEntity>)

    @Query("DELETE FROM forecast WHERE city = :city")
    fun deleteForecastByCity(city: String)
}