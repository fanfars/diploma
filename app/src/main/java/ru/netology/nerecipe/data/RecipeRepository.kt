package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.FilterState
import ru.netology.nerecipe.dto.Recipe
import java.util.ArrayList

interface RecipeRepository {

    val data: LiveData<List<Recipe>>
    fun like(recipeId: Long)
    fun share(recipeId: Long)
    fun delete(recipeId: Long)
    fun save(recipe: Recipe)
    fun moveRecipeToPosition(from: Int, to: Int)
    fun countOfRecipes(): Int
    fun getLastId(): Long
    fun clearFilter()
   // fun filterByCategory(filterState: List<String>)
    fun filterByFavorite()

    companion object {
        const val NEW_POST_ID = 0L
    }

}