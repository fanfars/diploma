package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.dto.CookingStep


interface StepInteractionListener {

    fun onDescriptionLongClicked(cookingStep: CookingStep)

}