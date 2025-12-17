package com.losad.fridgegenius.data.repo

import com.losad.fridgegenius.data.dao.FavoriteRecipeDao
import com.losad.fridgegenius.data.entity.FavoriteRecipeEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRecipeRepository @Inject constructor(
    private val dao: FavoriteRecipeDao
) {
    fun observeAll(): Flow<List<FavoriteRecipeEntity>> = dao.observeAll()

    suspend fun add(title: String, content: String) {
        dao.insert(FavoriteRecipeEntity(title = title, content = content))
    }

    suspend fun delete(id: Long) {
        dao.deleteById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}
