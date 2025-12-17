package com.losad.fridgegenius.data.repo

import com.losad.fridgegenius.data.dao.IngredientDao
import com.losad.fridgegenius.data.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IngredientRepository @Inject constructor(
    private val dao: IngredientDao
) {

    fun observeAll(): Flow<List<IngredientEntity>> = dao.observeAll()

    suspend fun add(
        name: String,
        quantity: Int,
        unit: String,
        expiryEpochDay: Long
    ) {
        dao.upsert(
            IngredientEntity(
                name = name.trim(),
                quantity = quantity,
                unit = unit,
                expiryEpochDay = expiryEpochDay
            )
        )
    }

    suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }
}
