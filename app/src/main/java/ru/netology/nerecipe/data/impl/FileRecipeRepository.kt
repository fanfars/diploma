package ru.netology.nerecipe.data.impl

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.dto.Recipe
import java.util.*
import kotlin.properties.Delegates

class FileRecipeRepository(
    private val application: Application
) : RecipeRepository {

    private val gson = Gson()
    private val type = TypeToken.getParameterized(List::class.java, Recipe::class.java).type

    private val prefs = application.getSharedPreferences(
        "repo", Context.MODE_PRIVATE
    )

    private var nextId: Long by Delegates.observable(
        prefs.getLong(NEXT_ID_PREFS_KEY, 0L)
    ) { _, _, newValue ->
        prefs.edit { putLong(NEXT_ID_PREFS_KEY, newValue) }
    }


    private var recipes
        get() = checkNotNull(data.value) { "Data value should not be null" }
        set(value) {
            application.openFileOutput(
                FILE_NAME, Context.MODE_PRIVATE
            ).bufferedWriter().use {
                it.write(gson.toJson(value))
            }
            data.value = value
        }

    override val data: MutableLiveData<List<Recipe>>

    init {
        val postsFile = application.filesDir.resolve(FILE_NAME)
        val recipes: List<Recipe> = if (postsFile.exists()) {
            val inputStream = application.openFileInput(FILE_NAME)
            val reader = inputStream.bufferedReader()
            reader.use { gson.fromJson(it, type) }
        } else emptyList()
        data = MutableLiveData(recipes)
    }

    override fun like(postId: Long) {
        recipes = recipes.map {
            if (it.id != postId) it else it.copy(
                isFavorite = !it.isFavorite,
                likes = if (it.isFavorite) it.likes - 1 else it.likes + 1
            )
        }
    }

    override fun share(postId: Long) {
        recipes = recipes.map {
            if (it.id != postId) it else it.copy(
                shares = it.shares + 1
            )
        }
    }

    override fun delete(postId: Long) {
        recipes = recipes.filterNot { it.id == postId }
    }

    override fun save(recipe: Recipe) {
        if (recipe.id == RecipeRepository.NEW_POST_ID) insert(recipe) else update(recipe)
    }

    private fun update(recipe: Recipe) {
        recipes = recipes.map {
            if (it.id == recipe.id) recipe else it
        }
    }

    private fun insert(recipe: Recipe) {
        recipes = listOf(
            recipe.copy(id = ++nextId)
        ) + recipes
    }

    override fun moveRecipeToPosition(from: Long, to: Long) {
        val destinationRecipe = recipes.first { it.id == to }
        val movableRecipe = recipes.first { it.id == from }
        Collections.swap(
            recipes,
            recipes.indexOf(destinationRecipe),
            recipes.indexOf(movableRecipe)
        )

    }

    override fun countOfRecipes(): Long {
        return recipes.size.toLong()
    }

    override fun getLastId(): Long {
        return nextId
    }

    private companion object {
        const val POSTS_PREFS_KEY = "posts"
        const val NEXT_ID_PREFS_KEY = "next_id"
        const val FILE_NAME = "posts.json"
    }

}