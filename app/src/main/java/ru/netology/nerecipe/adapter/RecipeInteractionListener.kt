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
    fun moveRecipe(from: Long, to: Long)
    fun recipeDown(recipeID: Long)
    fun recipeUp(recipeID: Long)
    fun onFavoriteClicked()
    fun onFilterClicked()
    }