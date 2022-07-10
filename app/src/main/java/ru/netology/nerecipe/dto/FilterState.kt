package ru.netology.nerecipe.dto

import kotlinx.serialization.Serializable

@Serializable
data class FilterState(
    var europeanIsActive: Boolean = true,
    var asianIsActive: Boolean = true,
    var panAsianIsActive: Boolean = true,
    var easternIsActive: Boolean = true,
    var americanIsActive: Boolean = true,
    var russianIsActive: Boolean = true,
    var mediterraneanIsActive: Boolean = true
)