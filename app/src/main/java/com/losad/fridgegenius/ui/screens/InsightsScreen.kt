package com.losad.fridgegenius.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 📊 인사이트 화면 (안정판)
 * - 외부 ViewModel / 아이콘 / 그래프 의존성 없음
 * - 발표/과제용으로 매우 안전
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("인사이트 📊") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ✅ 낭비 감소 인사이트
            InsightCard(
                title = "낭비 감소 지표 🌱",
                body = """
                    • 이번 주 예상 폐기 재료: 1개
                    • AI 추천으로 절약한 비용(예상): 4,500원
                    • 위험 재료 우선 사용률: 높음
                """.trimIndent()
            )

            // ✅ AI 배지 / 성과
            InsightCard(
                title = "AI 배지 🏅",
                body = """
                    • ‘임박 재료 구조자’ 배지 획득 가능
                    • AI 추천 기능을 적극 활용 중입니다
                """.trimIndent()
            )

            // ✅ 사용자 행동 분석
            InsightCard(
                title = "사용 패턴 분석 📈",
                body = """
                    • 유통기한 임박 재료를 잘 소비하고 있어요
                    • 레시피 추천 기능 사용 빈도: 높음
                    • 사진 인식 기능 활용 시작 단계
                """.trimIndent()
            )

            // ✅ 다음 업데이트 안내
            InsightCard(
                title = "다음 업데이트 🔥",
                body = """
                    • 위험도 변화 그래프 제공
                    • 알림(WorkManager)으로 ‘지금 먹어야 할 재료’ 안내
                    • AI 소비 습관 분석 리포트
                """.trimIndent()
            )
        }
    }
}

/**
 * 🔹 공통 카드 UI (Composable OK)
 */
@Composable
private fun InsightCard(
    title: String,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
