package ru.netology.nerecipe.dto

import kotlinx.serialization.Serializable
import java.math.RoundingMode
import java.text.DecimalFormat

@Serializable
data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    var likedByMe: Boolean = false,
    val postVideo: PostVideo?
)

@Serializable
class PostVideo(
    val title: String? = null,
    val cover: String? = null,
    val url: String? = null
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