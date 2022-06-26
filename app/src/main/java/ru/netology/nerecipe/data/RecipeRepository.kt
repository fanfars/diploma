package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface RecipeRepository {

    val data: LiveData<List<Recipe>>
    fun like(postId: Long)
    fun share(postId: Long)
    fun delete(postId: Long)
    fun save(recipe: Recipe)
    fun moveRecipeToPosition(from: Long, to: Long)
    fun countOfRecipes(): Long
    fun getLastId(): Long

    companion object {
        const val NEW_POST_ID = 0L
    }

}