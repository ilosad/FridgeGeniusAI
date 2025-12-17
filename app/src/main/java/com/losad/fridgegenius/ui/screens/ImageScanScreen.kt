package com.losad.fridgegenius.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.losad.fridgegenius.ml.IngredientLabelMapper
import com.losad.fridgegenius.ui.viewmodel.IngredientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageScanScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val ingredientVm: IngredientViewModel = hiltViewModel()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var recognizedIngredients by remember { mutableStateOf<List<String>>(emptyList()) }
    var selected by remember { mutableStateOf(setOf<String>()) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // 📸 갤러리 런처
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                imageUri = uri
                loading = true
                error = null
                recognizedIngredients = emptyList()
                selected = emptySet()

                try {
                    val bitmap =
                        androidx.core.graphics.drawable.DrawableCompat.wrap(
                            android.graphics.drawable.BitmapDrawable(
                                context.resources,
                                android.provider.MediaStore.Images.Media.getBitmap(
                                    context.contentResolver,
                                    uri
                                )
                            )
                        ).let {
                            (it as android.graphics.drawable.BitmapDrawable).bitmap
                        }

                    val image = InputImage.fromBitmap(bitmap, 0)

                    val labeler = ImageLabeling.getClient(
                        ImageLabelerOptions.DEFAULT_OPTIONS
                    )

                    labeler.process(image)
                        .addOnSuccessListener { labels ->
                            val labelTexts = labels
                                .filter { it.confidence >= 0.4f }
                                .map { it.text }

                            // ⭐ 3단계 핵심: 라벨 → 실제 재료
                            recognizedIngredients =
                                IngredientLabelMapper.mapLabelsToIngredients(labelTexts)

                            loading = false
                        }
                        .addOnFailureListener { e ->
                            error = "이미지 분석 실패: ${e.message}"
                            loading = false
                        }

                } catch (e: Exception) {
                    error = "이미지를 불러오지 못했어요: ${e.message}"
                    loading = false
                }
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("사진으로 재료 인식 📸") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "냉장고 또는 재료 사진을 선택하면\nAI가 재료 후보를 자동으로 인식해요.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { galleryLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("🖼️ 갤러리에서 사진 선택")
            }

            if (loading) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("AI가 사진을 분석 중이에요...")
                        CircularProgressIndicator()
                    }
                }
            }

            // ✅ 인식 결과
            if (recognizedIngredients.isNotEmpty()) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "🥬 인식된 재료 후보",
                            style = MaterialTheme.typography.titleMedium
                        )

                        recognizedIngredients.forEach { name ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(name)
                                Checkbox(
                                    checked = selected.contains(name),
                                    onCheckedChange = {
                                        selected =
                                            if (it) selected + name else selected - name
                                    }
                                )
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        selected.forEach { name ->
                            ingredientVm.addIngredient(
                                name = name,
                                quantity = 1,
                                unit = "개",
                                expiryDate = java.time.LocalDate.now().plusDays(5)
                            )
                        }
                        selected = emptySet()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selected.isNotEmpty()
                ) {
                    Text("➕ 선택한 재료 냉장고에 추가")
                }
            }

            // ❌ 에러
            if (error != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("⚠️ 오류", style = MaterialTheme.typography.titleMedium)
                        Text(error!!)
                    }
                }
            }
        }
    }
}
