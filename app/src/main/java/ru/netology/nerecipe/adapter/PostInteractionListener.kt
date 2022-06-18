package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.dto.Recipe

interface PostInteractionListener {

    fun onLikeClicked(recipe: Recipe)
    fun onShareClicked(recipe: Recipe)
    fun onViewClicked(recipe: Recipe)
    fun onRemoveClicked(recipe: Recipe)
    fun onEditClicked(recipe: Recipe)
    fun onPostClicked(recipe: Recipe)
    fun onUndoClicked()
    //fun onPlayVideoClicked(cookingSteps: CookingSteps)

}