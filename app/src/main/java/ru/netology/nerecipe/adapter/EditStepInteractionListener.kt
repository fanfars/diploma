package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.dto.CookingStep


interface EditStepInteractionListener {

    //fun onStepClicked(cookingStep: CookingStep)
    fun onStepRemoveClicked(step: CookingStep)
    fun onStepEditClicked(step: CookingStep)
    fun stepUp(position: Int)
    fun stepDown(position: Int)

}