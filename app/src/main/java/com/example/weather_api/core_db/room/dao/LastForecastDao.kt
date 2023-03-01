package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.LastForecastDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LastForecastDao {

    @Query("SELECT * FROM last_forecast ")
    fun getLastForecastFlow(): Flow<List<LastForecastDbEntity>>

    @Query("DELETE FROM last_forecast ")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastForecast(lastForecast: List<LastForecastDbEntity>)
}