package ru.netology.nerecipe.data.impl

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.MutableLiveData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.netology.nerecipe.data.FilterRepository
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.dto.FilterState
import ru.netology.nerecipe.dto.Recipe

class SharedPrefsRecipeRepository(
    application: Application
) : FilterRepository {

    private val prefs = application.getSharedPreferences(
        "repo", Context.MODE_PRIVATE
    )

    private var recipes
        get() = checkNotNull(recipesData.value) { "Data value should not be null" }
        set(value) {
            prefs.edit {
                val serializedPosts = Json.encodeToString(value)
                putString(RECIPE_PREFS_KEY, serializedPosts)
            }
            recipesData.value = value
        }

    private var categories
        get() = checkNotNull(categoryData.value) { "Data value should not be null" }
        set(value) {
            prefs.edit {
                val serializedPosts = Json.encodeToString(value)
                putString(CATEGORIES_PREFS_KEY, serializedPosts)
            }
            categoryData.value = value
        }

    private var query
        get() = checkNotNull(queryData.value) { "Data value should not be null" }
        set(value) {
            prefs.edit {
                val serializedQuery = Json.encodeToString(value)
                putString(QUERY_PREFS_KEY, serializedQuery)
            }
            queryData.value = value
        }

    override val recipesData: MutableLiveData<List<Recipe>>

    override val categoryData: MutableLiveData<FilterState>

    override val queryData: MutableLiveData<String>

    init {
        val serializedRecipes = prefs.getString(RECIPE_PREFS_KEY, null)
        val recipes: List<Recipe> = if (serializedRecipes != null) {
            Json.decodeFromString(serializedRecipes)
        } else emptyList()
        recipesData = MutableLiveData(recipes)

        val serializedCategory = prefs.getString(CATEGORIES_PREFS_KEY, null)
        val category: FilterState = if (serializedCategory != null) {
            Json.decodeFromString(serializedCategory)
        } else FilterState()
        categoryData = MutableLiveData(category)

        val serializedQuery = prefs.getString(QUERY_PREFS_KEY, null)
        val query: String = if (serializedQuery != null) {
            Json.decodeFromString(serializedQuery)
        } else ""
        queryData = MutableLiveData(query)
    }

    override fun save(recipe: Recipe) {
        if (recipe.id == RecipeRepository.NEW_POST_ID) insert(recipe) else update(recipe)
    }


    override fun update(recipe: Recipe) {
        recipes = recipes.map {
            if (it.id == recipe.id) recipe else it
        }
    }

    override fun insert(recipe: Recipe) {
        recipes = emptyList<Recipe>() + recipe
    }

    override fun saveCategories(filterState: FilterState) {
        categories = filterState
    }

    override fun saveQuery(inputQuery: String) {
        query = inputQuery
    }

    private companion object {
        const val RECIPE_PREFS_KEY = "recipe"
        const val CATEGORIES_PREFS_KEY = "categories"
        const val QUERY_PREFS_KEY = "query"

    }

}