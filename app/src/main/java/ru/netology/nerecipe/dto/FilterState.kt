package ru.netology.nerecipe.dto

class FilterState(
    var europeanIsActive: Boolean = false,
    var asianIsActive:Boolean = false,
    var panAsianIsActive:Boolean = false,
    var easternIsActive:Boolean = false,
    var americanIsActive:Boolean = false,
    var russianIsActive:Boolean = false,
    var mediterraneanIsActive:Boolean = false
) {
}