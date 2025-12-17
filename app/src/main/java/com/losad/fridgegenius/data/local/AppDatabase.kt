package com.losad.fridgegenius.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.losad.fridgegenius.data.dao.FavoriteRecipeDao
import com.losad.fridgegenius.data.dao.IngredientDao
import com.losad.fridgegenius.data.entity.FavoriteRecipeEntity
import com.losad.fridgegenius.data.entity.IngredientEntity

@Database(
    entities = [
        IngredientEntity::class,
        FavoriteRecipeEntity::class
    ],
    version = 3, // ✅ 기존 2 -> 3 (테이블 추가했으니 반드시 올려야 함)
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
    abstract fun favoriteRecipeDao(): FavoriteRecipeDao
}
