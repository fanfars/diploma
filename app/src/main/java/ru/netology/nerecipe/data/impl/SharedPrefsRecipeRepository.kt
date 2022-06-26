package ru.netology.nerecipe.data.impl

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.dto.Recipe
import kotlin.properties.Delegates

class SharedPrefsRecipeRepository(
    application: Application
)  {

    private val prefs = application.getSharedPreferences(
        "repo", Context.MODE_PRIVATE
    )

//    private var nextId: Long by Delegates.observable(
//        prefs.getLong(NEXT_ID_PREFS_KEY, 0L)
//    ) { _, _, newValue ->
//        prefs.edit { putLong(NEXT_ID_PREFS_KEY, newValue) }
//    }


    private var recipes
        get() = checkNotNull(data.value) { "Data value should not be null" }
        set(value) {
            prefs.edit {
                val serializedPosts = Json.encodeToString(value)
                putString(POSTS_PREFS_KEY, serializedPosts)
            }
            data.value = value
        }

    val data: MutableLiveData<List<Recipe>>

    init {
        val serializedRecipes = prefs.getString(POSTS_PREFS_KEY, null)
        val recipes: List<Recipe> = if (serializedRecipes != null) {
            Json.decodeFromString(serializedRecipes)
        } else emptyList()
        data = MutableLiveData(recipes)
    }

   fun like(recipesId: Long) {
        recipes = recipes.map {
            if (it.id != recipesId) it else it.copy(
                isFavorite = !it.isFavorite,
                likes = if (it.isFavorite) it.likes - 1 else it.likes + 1
            )
        }
    }

    fun share(recipesId: Long) {
        recipes = recipes.map {
            if (it.id != recipesId) it else it.copy(
                shares = it.shares + 1
            )
        }
    }

    fun delete() {
        recipes = emptyList()
    }

    fun save(recipe: Recipe) {
        if (recipe.id == RecipeRepository.NEW_POST_ID) insert(recipe) else update(recipe)
    }


    private fun update(recipe: Recipe) {
        recipes = recipes.map {
            if (it.id == recipe.id) recipe else it
        }
    }

    private fun insert(recipe: Recipe) {
        recipes = emptyList<Recipe>() + recipe
    }

    private companion object {
        const val POSTS_PREFS_KEY = "posts"
        //const val NEXT_ID_PREFS_KEY = "next_id"
    }

}