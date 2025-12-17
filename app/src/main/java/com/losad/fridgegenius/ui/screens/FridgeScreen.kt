package com.losad.fridgegenius.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.losad.fridgegenius.data.entity.IngredientEntity
import com.losad.fridgegenius.ui.viewmodel.IngredientViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeScreen(
    onGoAddIngredient: () -> Unit,
    vm: IngredientViewModel = hiltViewModel()
) {
    val items by vm.items.collectAsState()
    var query by remember { mutableStateOf("") }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("내 냉장고 🧊") },
                actions = {
                    IconButton(onClick = onGoAddIngredient) {
                        Text("➕")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fridgeBackground())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("재료 검색 🔎") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items.filter { it.name.contains(query, true) },
                    key = { it.id }
                ) { item ->
                    IngredientCard(
                        item = item,
                        onDelete = { vm.deleteIngredient(item.id) },
                        vm = vm
                    )
                }
            }
        }
    }
}

/* -----------------------------------------------------
   🍓 고급 IngredientCard
----------------------------------------------------- */

@Composable
private fun IngredientCard(
    item: IngredientEntity,
    onDelete: () -> Unit,
    vm: IngredientViewModel
) {
    val today = LocalDate.now()
    val expiry = LocalDate.ofEpochDay(item.expiryEpochDay)
    val daysLeft = ChronoUnit.DAYS.between(today, expiry).toInt()
    val risk = vm.calcRiskScore(item.expiryEpochDay)

    val (emoji, label, bg) = when {
        risk >= 90 -> Triple("☠️", "폐기 권장", Color(0xFFFFE4E4))
        risk >= 70 -> Triple("⚠️", "위험", Color(0xFFFFF1D6))
        risk >= 50 -> Triple("⏰", "임박", Color(0xFFFFF8CC))
        else -> Triple("✅", "안전", Color(0xFFEAF7EC))
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(bg)
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("수량: ${item.quantity}${item.unit}")
                Text("유통기한: $expiry")
                Text("위험도 ${risk}점 · $label")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(emoji, style = MaterialTheme.typography.headlineMedium)

                Spacer(modifier = Modifier.height(6.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = Color(0xFFFFDAD6),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = Color(0xFFB3261E)
                    )
                }

                Text("${daysLeft}일", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

/* -----------------------------------------------------
   🎨 전체 배경
----------------------------------------------------- */

@Composable
private fun fridgeBackground(): Brush =
    Brush.verticalGradient(
        listOf(
            Color(0xFFFFF7FB),
            Color(0xFFF3F7FF)
        )
    )
