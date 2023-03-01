package com.example.weather_api.core_db.room.dao

import androidx.room.*
import com.example.weather_api.core_db.room.entitity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorites")
    fun getFavoritesFlow(): Flow<List<FavoritesDbEntity>>

    @Query("SELECT * FROM favorites WHERE city = :city")
    fun getFavorites(city: String): FavoritesDbEntity

    @Query("SELECT EXISTS( SELECT city  FROM favorites WHERE city = :city)")
    fun checkForFavorites(city: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: FavoritesDbEntity)

    @Query("DELETE FROM favorites WHERE city = :city")
    suspend fun deleteFavorites(city: String)

    @Update(entity = FavoritesDbEntity::class)
    suspend fun updateFavorites(weather: UpdateFavoritesTuple)
}