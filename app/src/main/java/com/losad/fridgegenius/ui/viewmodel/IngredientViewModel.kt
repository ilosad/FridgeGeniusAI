package com.losad.fridgegenius.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.losad.fridgegenius.data.entity.IngredientEntity
import com.losad.fridgegenius.data.repo.IngredientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt


@HiltViewModel
class IngredientViewModel @Inject constructor(
    private val repo: IngredientRepository
) : ViewModel() {

    /**
     * ✅ DB 전체 재료 목록
     */
    val items: StateFlow<List<IngredientEntity>> =
        repo.observeAll()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    /**
     * ✅ 재료 수동 추가
     */
    fun addIngredient(
        name: String,
        quantity: Int,
        unit: String,
        expiryDate: LocalDate
    ) {
        viewModelScope.launch {
            repo.add(
                name = name,
                quantity = quantity,
                unit = unit,
                expiryEpochDay = expiryDate.toEpochDay()
            )
        }
    }

    /**
     * ✅ 📸 사진 인식 결과로 재료 자동 추가
     *
     * - ImageScanScreen에서 인식된 label 리스트를 그대로 넘김
     * - 기본값: 수량 1, 단위 개, 유통기한 +3일
     * - 중복 이름은 자동 제거
     */
    fun addIngredientsFromPhoto(
        detectedNames: List<String>
    ) {
        if (detectedNames.isEmpty()) return

        val today = LocalDate.now()

        viewModelScope.launch {
            detectedNames
                .map { it.trim().lowercase() }
                .filter { it.isNotBlank() }
                .distinct()
                .forEach { name ->
                    repo.add(
                        name = name,
                        quantity = 1,
                        unit = "개",
                        expiryEpochDay = today.plusDays(3).toEpochDay()
                    )
                }
        }
    }

    /**
     * ✅ 삭제
     */
    fun deleteIngredient(id: Long) {
        viewModelScope.launch {
            repo.deleteById(id)
        }
    }

    /**
     * 🧠 위험도 점수 계산 (0~100)
     *
     * 👉 설명용으로 아주 좋음
     * - 남은 날짜 기반
     * - 구간별 단계적 증가
     */
    fun calcRiskScore(expiryEpochDay: Long): Int {
        val todayEpoch = LocalDate.now().toEpochDay()
        val daysLeft = (expiryEpochDay - todayEpoch).toInt()

        // ❌ 이미 지난 재료
        if (daysLeft <= 0) {
            val extra = (-daysLeft).coerceAtMost(10)
            return (90 + extra).coerceIn(90, 100)
        }

        // ⚠️ 1~2일
        if (daysLeft <= 2) {
            return (70 + (2 - daysLeft) * 10).coerceIn(70, 89)
        }

        // ⏰ 3~7일
        if (daysLeft <= 7) {
            val t = (7 - daysLeft).toFloat() / 4f
            return (40 + t * 29f).roundToInt().coerceIn(40, 69)
        }

        // ✅ 8일 이상
        val normalized =
            (daysLeft.coerceAtMost(30) - 8).toFloat() / 22f
        return (39 - normalized * 39f).roundToInt().coerceIn(0, 39)
    }

    /**
     * 🚨 오늘 꼭 써야 할 위험 재료 TOP N
     *
     * 👉 Home / AI / 알림에서 공용 사용
     */
    fun observeDangerIngredients(
        threshold: Int = 70,
        topN: Int = 3
    ): StateFlow<List<DangerIngredient>> =
        items
            .map { list ->
                list.map { entity ->
                    DangerIngredient(
                        id = entity.id,
                        name = entity.name,
                        quantity = entity.quantity,
                        unit = entity.unit,
                        expiryEpochDay = entity.expiryEpochDay,
                        riskScore = calcRiskScore(entity.expiryEpochDay)
                    )
                }
                    .filter { it.riskScore >= threshold }
                    .sortedByDescending { it.riskScore }
                    .take(topN)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )
}

/**
 * ✅ 위험 재료 전용 모델
 * → UI / GPT / Gemini / 알림에 그대로 사용
 */
data class DangerIngredient(
    val id: Long,
    val name: String,
    val quantity: Int,
    val unit: String,
    val expiryEpochDay: Long,
    val riskScore: Int
)