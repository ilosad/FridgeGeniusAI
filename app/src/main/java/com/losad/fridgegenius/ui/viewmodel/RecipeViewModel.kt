package com.losad.fridgegenius.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.losad.fridgegenius.data.entity.FavoriteRecipeEntity
import com.losad.fridgegenius.data.entity.IngredientEntity
import com.losad.fridgegenius.data.repo.FavoriteRecipeRepository
import com.losad.fridgegenius.data.repo.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val favoriteRepo: FavoriteRecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecipeUiState>(RecipeUiState.Idle)
    val uiState: StateFlow<RecipeUiState> = _uiState

    val favorites: StateFlow<List<FavoriteRecipeEntity>> =
        favoriteRepo.observeAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun reset() {
        _uiState.value = RecipeUiState.Idle
    }

    /**
     * ✅ TOP 위험 재료 기반 레시피 생성
     */
    fun generateFromTopRisk(
        topRisk: List<Pair<IngredientEntity, Int>>
    ) {
        if (topRisk.isEmpty()) {
            _uiState.value = RecipeUiState.Error("위험 재료가 없습니다.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = RecipeUiState.Loading
            try {
                val prompt = recipeRepository.buildPromptFromTopRisk(topRisk)
                val result = recipeRepository.generateRecipeText(prompt)
                _uiState.value = RecipeUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value =
                    RecipeUiState.Error(e.message ?: "레시피 생성 중 오류가 발생했어요.")
            }
        }
    }

    /**
     * ✅ 사용자가 선택한 재료(1~5개) 기반 레시피 생성
     */
    fun generateFromSelected(
        ingredients: List<String>
    ) {
        val cleaned = ingredients
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        if (cleaned.isEmpty()) {
            _uiState.value = RecipeUiState.Error("재료를 선택해 주세요.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = RecipeUiState.Loading
            try {
                val prompt = recipeRepository.buildPromptFromSelected(cleaned)
                val result = recipeRepository.generateRecipeText(prompt)
                _uiState.value = RecipeUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value =
                    RecipeUiState.Error(e.message ?: "레시피 생성 중 오류가 발생했어요.")
            }
        }
    }

    // ✅ 즐겨찾기
    fun saveFavorite(title: String, content: String) {
        viewModelScope.launch {
            favoriteRepo.add(title = title, content = content)
        }
    }

    fun deleteFavorite(id: Long) {
        viewModelScope.launch {
            favoriteRepo.delete(id)
        }
    }

    fun clearFavorites() {
        viewModelScope.launch {
            favoriteRepo.clearAll()
        }
    }
}