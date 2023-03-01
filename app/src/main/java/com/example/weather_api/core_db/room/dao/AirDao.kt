package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.AirDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AirDao {

    @Query("SELECT * FROM air WHERE city = :city")
    fun getAirFlow(city: String): Flow<AirDbEntity>

    @Query("SELECT * FROM air WHERE city = :city")
    fun getAir(city: String): AirDbEntity

    @Insert
    suspend fun insertAir(Air: AirDbEntity)

    @Query("DELETE FROM air WHERE city = :city")
    fun deleteAirByCity(city: String)
}