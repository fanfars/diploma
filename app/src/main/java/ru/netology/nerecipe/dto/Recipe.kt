package ru.netology.nerecipe.dto

import kotlinx.serialization.Serializable
import java.math.RoundingMode
import java.text.DecimalFormat

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

fun countFormat(count: Int): String {
    return when (count) {
        in 1..999 -> "${count.toString()}"
        in 1000..1099 -> "${roundNoDecimal(count.toDouble() / 1_000.0)}K"
        in 1100..9_999 -> "${roundWithDecimal(count.toDouble() / 1_000.0)}K"
        in 10_000..999_999 -> "${roundNoDecimal(count.toDouble() / 1_000.0)}K"
        in 1_000_000..1_099_999 -> "${roundNoDecimal(count.toDouble() / 1_000_000.0)}M"
        in 1_100_000..Int.MAX_VALUE -> "${roundWithDecimal(count.toDouble() / 1_000_000.0)}M"
        else -> count.toString()
    }
}

fun roundWithDecimal(number: Double): Double {
    val df = DecimalFormat("#.#")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toDouble()
}

fun roundNoDecimal(number: Double): Int {
    val df = DecimalFormat("#")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toInt()
}