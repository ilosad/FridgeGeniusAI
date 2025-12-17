package com.losad.fridgegenius.di

import android.content.Context
import androidx.room.Room
import com.losad.fridgegenius.data.dao.FavoriteRecipeDao
import com.losad.fridgegenius.data.dao.IngredientDao
import com.losad.fridgegenius.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fridgegenius.db"
        )
            .fallbackToDestructiveMigration() // ✅ 개발중엔 이게 안전
            .build()
    }

    @Provides
    fun provideIngredientDao(db: AppDatabase): IngredientDao = db.ingredientDao()

    @Provides
    fun provideFavoriteRecipeDao(db: AppDatabase): FavoriteRecipeDao = db.favoriteRecipeDao()
}
