package com.losad.fridgegenius.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.losad.fridgegenius.data.entity.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRecipeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteRecipeEntity)

    @Query("SELECT * FROM favorite_recipes ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<FavoriteRecipeEntity>>

    @Query("DELETE FROM favorite_recipes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM favorite_recipes")
    suspend fun clearAll()
}
