package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.LastLocationDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LastLocationDao {

    @Query("SELECT * FROM last_location WHERE last_location_key =:key")
    suspend fun getLastLocations(key: String): LastLocationDbEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLastLocation(lastLocation: LastLocationDbEntity)
}