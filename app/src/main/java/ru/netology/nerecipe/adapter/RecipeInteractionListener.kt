package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.dto.Recipe

interface RecipeInteractionListener {

    fun onLikeClicked(recipe: Recipe)
    fun onShareClicked(recipe: Recipe)
    fun onRemoveClicked(recipe: Recipe)
    fun onEditClicked(recipe: Recipe)
    fun onPostClicked(recipe: Recipe)
    fun onUndoClicked()
    fun removeRecipeById(recipeID: Long)
    fun moveRecipe(from: Int, to: Int)
    fun recipeDown(position: Int)
    fun recipeUp(position: Int)
    fun onFavoriteClicked()
    fun onFilterClicked()
    fun saveRecipeWithSteps(recipe: Recipe)
    //fun onAddStepClicked(list: List<Long>)


}