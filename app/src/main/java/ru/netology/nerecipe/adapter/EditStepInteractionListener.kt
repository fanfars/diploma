package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.dto.CookingStep



interface EditStepInteractionListener {

    //fun onStepClicked(cookingStep: CookingStep)
    fun onStepRemoveClicked(cookingStep: CookingStep)
    fun onStepEditClicked(cookingStep: CookingStep)

}