package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface RecipeRepository {

    val data: LiveData<List<Recipe>>
    fun like(recipeId: Long)
    fun share(recipeId: Long)
    fun delete(recipeId: Long)
    fun save(recipe: Recipe)
    fun moveRecipeToPosition(from: Long, to: Long)
    fun countOfRecipes(): Long
    fun getLastId(): Long
    fun favorite(recipeId: Long)

    companion object {
        const val NEW_POST_ID = 0L
    }

}