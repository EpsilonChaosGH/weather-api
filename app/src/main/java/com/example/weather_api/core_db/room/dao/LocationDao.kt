package com.example.weather_api.core_db.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.weather_api.core_db.room.entitity.LocationDB

@Dao
interface LocationDao {
    @Query("SELECT * FROM favorite_locations")
    suspend fun getAll(): List<LocationDB>

    @Insert
    suspend fun insertAll(vararg users: LocationDB)

    @Query("DELETE FROM favorite_locations WHERE city = :city")
    suspend fun delete(city: String)
}
