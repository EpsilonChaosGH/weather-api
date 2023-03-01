package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.LastAirDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LastAirDao {

    @Query("SELECT * FROM last_air WHERE last_weather_key = :last_weather_key")
    fun getLastAirFlow(last_weather_key: String): Flow<LastAirDbEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastAir(lastAir: LastAirDbEntity)
}