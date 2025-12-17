package com.losad.fridgegenius.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIngredientScreen(
    initialName: String? = null,
    onSave: (name: String, quantity: Int, unit: String, expiry: LocalDate) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }

    // ✅ 수량/단위
    var quantityText by remember { mutableStateOf("1") }
    var unit by remember { mutableStateOf("개") }

    var expiry by remember { mutableStateOf(LocalDate.now().plusDays(3)) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(initialName) {
        if (!initialName.isNullOrBlank()) name = initialName
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val picked = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        expiry = picked
                    }
                    showDatePicker = false
                }) { Text("선택") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("취소") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("재료 추가 🧺") },
                navigationIcon = { TextButton(onClick = onBack) { Text("←") } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ✅ 상단 안내 카드 (원래 느낌 유지)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("재료를 등록해볼까요? 🐣✨", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• 재료명 + 수량 + 유통기한 입력하면 바로 냉장고에 저장돼요!")
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("재료 이름 (예: 우유, 계란, 토마토) 🧊") }
            )

            // ✅ 수량/단위 입력 UI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { input ->
                        quantityText = input.filter { it.isDigit() }.take(4)
                    },
                    modifier = Modifier.weight(1f),
                    label = { Text("수량") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it.take(6) },
                    modifier = Modifier.weight(1f),
                    label = { Text("단위 (예: 개, g, ml)") },
                    singleLine = true
                )
            }

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) { Text("📅 유통기한 선택: $expiry") }

            Button(
                onClick = {
                    val trimmed = name.trim()
                    val q = quantityText.toIntOrNull()?.coerceAtLeast(1) ?: 1
                    val u = unit.trim().ifBlank { "개" }

                    if (trimmed.isNotEmpty()) {
                        onSave(trimmed, q, u, expiry)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("✅ 냉장고에 저장하기") }

            // ✅ 하단 “다음 단계” 카드도 유지
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("다음 단계에서 더 강해져요 💥", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• 부패 위험도 점수로 ‘지금 써야 할 재료’ 판단")
                    Text("• 위험 재료 기반 레시피 자동 생성(GPT/Gemini)")
                }
            }
        }
    }
}
