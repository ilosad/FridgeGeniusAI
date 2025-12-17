package com.losad.fridgegenius.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.losad.fridgegenius.ui.components.TypewriterText
import com.losad.fridgegenius.ui.viewmodel.IngredientViewModel
import com.losad.fridgegenius.ui.viewmodel.RecipeUiState
import com.losad.fridgegenius.ui.viewmodel.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeHubScreen(
    onBack: () -> Unit = {}
) {
    val ingredientVm: IngredientViewModel = hiltViewModel()
    val recipeVm: RecipeViewModel = hiltViewModel()

    val items by ingredientVm.items.collectAsState()
    val uiState by recipeVm.uiState.collectAsState()
    val favorites by recipeVm.favorites.collectAsState()

    // ✅ TOP 위험 재료 3개
    val topRisk = items
        .map { it to ingredientVm.calcRiskScore(it.expiryEpochDay) }
        .sortedByDescending { it.second }
        .take(3)

    val topNames = topRisk.map { (item, _) -> "${item.name} (${item.quantity}${item.unit})" }

    // ✅ 선택 재료(1~5)
    var selected by remember { mutableStateOf(setOf<String>()) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("AI 레시피 추천 🍳") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /* ---------- TOP 재료 카드 ---------- */
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("오늘 꼭 써야 할 TOP 재료", style = MaterialTheme.typography.titleMedium)

                    if (topRisk.isEmpty()) {
                        Text("재료가 없어요. 냉장고 탭에서 재료를 먼저 추가해 주세요!")
                    } else {
                        topRisk.forEach { (item, risk) ->
                            Text("• ${item.name} (${item.quantity}${item.unit}) / 위험도 ${risk}점")
                        }
                    }
                }
            }

            Button(
                onClick = { recipeVm.generateFromTopRisk(topRisk) },
                modifier = Modifier.fillMaxWidth(),
                enabled = topRisk.isNotEmpty() && uiState !is RecipeUiState.Loading
            ) {
                Text("🤖 TOP 재료로 레시피 생성하기")
            }

            /* ---------- 선택 재료 카드 ---------- */
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("직접 재료 선택 (1~5개) 🥗", style = MaterialTheme.typography.titleMedium)

                    if (items.isEmpty()) {
                        Text("선택할 재료가 없어요.")
                    } else {
                        items.forEach { item ->
                            val name = item.name
                            val checked = selected.contains(name)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("$name (${item.quantity}${item.unit})")
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { on ->
                                        selected = when {
                                            on && selected.size < 5 -> selected + name
                                            !on -> selected - name
                                            else -> selected
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = { recipeVm.generateFromSelected(selected.toList()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = selected.isNotEmpty() && uiState !is RecipeUiState.Loading
            ) {
                Text("🧠 선택 재료로 레시피 생성")
            }

            /* ---------- 결과 영역 ---------- */
            when (uiState) {
                is RecipeUiState.Idle -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("버튼을 누르면 AI가 레시피 2개를 만들어줘요 ✨")
                            if (topNames.isNotEmpty()) {
                                Text("TIP: 위험도가 높은 재료부터 쓰면 낭비가 줄어요!")
                            }
                        }
                    }
                }

                is RecipeUiState.Loading -> {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("AI가 레시피 생성 중...")
                            CircularProgressIndicator()
                        }
                    }
                }

                is RecipeUiState.Success -> {
                    val text = (uiState as RecipeUiState.Success).text

                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("✅ 추천 결과", style = MaterialTheme.typography.titleMedium)

                            // ⭐⭐⭐ 옵션 1 핵심: 타이핑 애니메이션
                            TypewriterText(fullText = text)
                        }
                    }

                    Button(
                        onClick = { recipeVm.saveFavorite("AI 추천 레시피", text) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("❤️ 즐겨찾기 저장") }

                    Button(
                        onClick = { recipeVm.reset() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("다시 추천받기") }
                }

                is RecipeUiState.Error -> {
                    val msg = (uiState as RecipeUiState.Error).message
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("⚠️ 오류", style = MaterialTheme.typography.titleMedium)
                            Text(msg)
                        }
                    }

                    Button(
                        onClick = { recipeVm.reset() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("닫기") }
                }
            }

            /* ---------- 즐겨찾기 ---------- */
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("즐겨찾기 ❤️", style = MaterialTheme.typography.titleMedium)

                    if (favorites.isEmpty()) {
                        Text("아직 저장된 레시피가 없어요.")
                    } else {
                        favorites.take(5).forEach { fav ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(fav.title)
                                        IconButton(onClick = { recipeVm.deleteFavorite(fav.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "삭제")
                                        }
                                    }
                                    Text(fav.content)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
