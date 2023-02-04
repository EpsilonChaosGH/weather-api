package com.example.weather_api.core_db.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weather_api.core_db.room.entitity.LocationDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM favorite_locations")
    fun getAllLocationsFlow(): Flow<List<LocationDbEntity?>>

    @Query("SELECT * FROM favorite_locations")
    fun getAllLocations(): List<LocationDbEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationDbEntity)

    @Query("DELETE FROM favorite_locations WHERE city = :city")
    suspend fun deleteLocation(city: String)
}
