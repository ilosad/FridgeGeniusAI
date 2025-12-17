package com.losad.fridgegenius.data.repo

import com.losad.fridgegenius.data.entity.IngredientEntity
import com.losad.fridgegenius.data.remote.OpenAiRecipeClient
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipeRepository @Inject constructor() {

    /**
     * ✅ TOP 위험 재료 기반 프롬프트 생성
     */
    fun buildPromptFromTopRisk(
        topRiskIngredients: List<Pair<IngredientEntity, Int>>
    ): String {
        val today = LocalDate.now()

        val lines = topRiskIngredients.joinToString("\n") { (item, risk) ->
            val expiry = LocalDate.ofEpochDay(item.expiryEpochDay)
            val daysLeft = ChronoUnit.DAYS.between(today, expiry).toInt()
            "- ${item.name} (${item.quantity}${item.unit}), 남은 ${daysLeft}일, 위험도 ${risk}점"
        }

        return """
        아래는 내 냉장고 재료 중 유통기한이 임박한 TOP 재료 목록이다.
        이 재료를 최대한 활용해서 오늘 바로 만들 수 있는 레시피 2가지를 추천해줘.

        [재료 목록]
        $lines

        [요구사항]
        1) 레시피 2개 추천
        2) 조리 시간(분), 난이도(쉬움/보통/어려움), 1인분 기준
        3) 재료 손질 → 조리 순서 단계별 작성
        4) 마지막에 "추가로 있으면 좋은 재료", "대체 가능한 재료" 분리

        [출력 포맷]
        ✅ 레시피 #1:
        - 예상 시간:
        - 난이도:
        - 사용 재료:
        - 조리 단계:
        - 팁:

        ✅ 레시피 #2:
        - 예상 시간:
        - 난이도:
        - 사용 재료:
        - 조리 단계:
        - 팁:
        """.trimIndent()
    }

    /**
     * ✅ 사용자가 선택한 재료(1~5개) 기반 프롬프트
     */
    fun buildPromptFromSelected(
        ingredients: List<String>
    ): String {
        return """
        다음 재료를 사용해서 현실적으로 만들 수 있는 레시피 2가지를 추천해줘.

        [재료]
        ${ingredients.joinToString(", ")}

        [조건]
        - 집에서 만들 수 있는 요리
        - 한국어
        - 위와 동일한 출력 포맷 유지
        """.trimIndent()
    }

    /**
     * ✅ OpenAI 호출 (동기 함수, 반드시 IO 스레드에서 호출해야 함)
     */
    fun generateRecipeText(prompt: String): String {
        return OpenAiRecipeClient.generateRecipeText(prompt)
    }
}