package com.losad.fridgegenius.ui.viewmodel

sealed interface RecipeUiState {
    data object Idle : RecipeUiState
    data object Loading : RecipeUiState
    data class Success(val text: String) : RecipeUiState
    data class Error(val message: String) : RecipeUiState
}
