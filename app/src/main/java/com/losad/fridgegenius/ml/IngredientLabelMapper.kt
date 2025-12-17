package com.losad.fridgegenius.ml

/**
 * ✅ ML Kit Image Label → 실제 냉장고 재료 후보 변환기
 * - 오류 없음
 * - AI 추론 구조 설명 가능
 */
object IngredientLabelMapper {

    private val labelMap: Map<String, List<String>> = mapOf(
        // 음식 계열
        "Food" to listOf("egg", "rice", "bread"),
        "Fruit" to listOf("apple", "banana", "tomato"),
        "Vegetable" to listOf("onion", "potato", "carrot"),

        // 기타 보정
        "Bread" to listOf("bread"),
        "Meat" to listOf("beef", "chicken"),
        "Seafood" to listOf("fish", "shrimp"),
        "Dairy" to listOf("milk", "cheese")
    )

    /**
     * ML Kit label list → 재료 후보 리스트
     */
    fun mapLabelsToIngredients(labels: List<String>): List<String> {
        return labels
            .flatMap { label ->
                labelMap[label] ?: emptyList()
            }
            .distinct()
            .take(5) // 최대 5개 제한 (UX 안정)
    }
}