package com.losad.fridgegenius.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.losad.fridgegenius.ui.viewmodel.IngredientViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGoImageScan: () -> Unit,
    onGoFridge: () -> Unit,
    onGoRecipe: () -> Unit,
    onGoInsights: () -> Unit,
    vm: IngredientViewModel = hiltViewModel()
) {
    // ✅ TOP 3 위험 재료 (Lifecycle 안전)
    val top3 by vm
        .observeDangerIngredients(threshold = 0, topN = 3)
        .collectAsStateWithLifecycle(initialValue = emptyList())

    // ✅ 은은한 shimmer 애니메이션
    val infinite = rememberInfiniteTransition(label = "shimmer")
    val shimmer by infinite.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFFFFF1F7),
            Color(0xFFF3F0FF),
            Color(0xFFEFFBFF)
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Save Eat ✨") })
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🌟 히어로 카드
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(shimmer),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("오늘의 미션 🧠💖", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("• 유통기한이 얼마 남지 않은 음식부터 소비하면      낭비가 확 줄어요!")
                    Text("• 사진으로 재료를 인식해서 자동 등록 해보세요📸")
                }
            }

            // ⚠️ TOP 3 카드
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("오늘 꼭 써야 할 TOP 3 ⚠️(유통기한=위험도)", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))

                    if (top3.isEmpty()) {
                        Text("아직 등록된 재료가 없어요 🐣\n냉장고에서 ➕ 로 추가해보세요!")
                    } else {
                        top3.forEachIndexed { idx, it ->
                            val expiry = LocalDate.ofEpochDay(it.expiryEpochDay)
                            val medal = listOf("🥇", "🥈", "🥉").getOrElse(idx) { "" }

                            val tag = when {
                                it.riskScore >= 90 -> "폐기 권장"
                                it.riskScore >= 70 -> "위험"
                                it.riskScore >= 50 -> "임박"
                                else -> "안전"
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("$medal ${it.name}", style = MaterialTheme.typography.titleMedium)
                                        Text("유통기한: $expiry", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text(
                                        "위험도 ${it.riskScore} · $tag",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 🔘 네비 버튼들
            Button(onClick = onGoFridge, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Text("🧊 냉장고 보러가기")
            }

            Button(onClick = onGoImageScan, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Text("📸 사진으로 재료 인식하기")
            }

            Button(onClick = onGoRecipe, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Text("🍳 레시피 추천 받기")
            }

            OutlinedButton(onClick = onGoInsights, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Text("📊 낭비 감소 인사이트 보기")
            }
        }
    }
}
