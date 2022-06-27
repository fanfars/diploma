package ru.netology.nerecipe.dto

import kotlinx.serialization.Serializable

@Serializable
data class Recipe(
    val id: Long,
    val author: String,
    val title: String,
    val description: String,
    val category: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val cookingTime: Int = 0,
    val steps: List<CookingStep>,
    var isFavorite: Boolean = false
)


@Serializable
data class CookingStep(
    val stepDescription: String,
    val stepTime: Int,
    val cover: String? = null,
    var stepNumber: Int
)