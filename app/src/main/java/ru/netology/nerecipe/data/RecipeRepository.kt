package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface RecipeRepository {

    val data: LiveData<List<Recipe>>
    fun like(recipeId: Long)
    fun delete(recipeId: Long)
    fun save(recipe: Recipe)
    fun moveRecipeToPosition(from: Int, to: Int)
    fun countOfRecipes(): Int
    fun getLastId(): Long
    fun clearFilter()
    fun filterByFavorite()

    companion object {
        const val NEW_POST_ID = 0L
    }

}