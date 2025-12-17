package com.losad.fridgegenius.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,

    // ✅ 새로 추가
    val quantity: Int,          // 개수
    val unit: String,           // 단위 (개, ml, g)

    val expiryEpochDay: Long,
    val createdAt: Long = System.currentTimeMillis()
)
