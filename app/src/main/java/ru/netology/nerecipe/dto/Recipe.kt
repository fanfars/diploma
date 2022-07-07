package ru.netology.nerecipe.dto

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Long,
    val author: String,
    val title: String,
    val description: String,
    val category: String,
    val steps: List<CookingStep>,
    var cookingTime: Int = 0,
    var isFavorite: Boolean = false
) {
    companion object {
        enum class Categories { European, Asian, PanAsian, Eastern, American, Russian, Mediterranean }
    }
}

@Serializable
data class CookingStep(
    val stepDescription: String,
    val stepTime: Int,
    val cover: String? = null,
    var stepNumber: Int
)

