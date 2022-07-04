package ru.netology.nerecipe.viewModel

import android.app.Application
import android.content.ContentResolver.SCHEME_ANDROID_RESOURCE
import android.provider.Settings.Global.getString
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.R
import ru.netology.nerecipe.data.impl.FileRecipeRepository
import ru.netology.nerecipe.dto.FilterState
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.SingleLiveEvent
import java.util.*
import kotlin.collections.ArrayList

class FilterRecipeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = FileRecipeRepository(application)

    val data by repository::data
    val navigateToFeedFragment = SingleLiveEvent<Unit>()
    val recipeToDesign = MutableLiveData<Recipe?>(null)
    val filteredRecipes = MediatorLiveData<List<Recipe>?>()
    val currentRecipe = MutableLiveData<Recipe?>(null)
    var favoriteFilter = MutableLiveData<Boolean>()
    val categoriesList = MutableLiveData<List<String>>()
    val searchQuery = MutableLiveData<String>()
    val shareRecipeContent = SingleLiveEvent<String>()
    val navigateToRecipeScreen = SingleLiveEvent<Long>()
    var filterState = FilterState()

    init {
        favoriteFilter.value = false

        filteredRecipes.addSource(favoriteFilter) {
            val searchText =
                searchQuery.value?.lowercase(Locale.getDefault())?.trim { it <= ' ' } ?: ""
            if (searchText.isNotEmpty() || data.value != null || categoriesList.value != null) {
                val list = data.value?.filter { recipe ->
                    recipe.title.lowercase(Locale.getDefault()).contains(searchText)
                }?.filter {
                    toFilterStates(filterState).forEach { category ->
                        if (it.category!!.contains(category)) return@filter true
                    }
                    return@filter false
                }
                val listFavorite = if (favoriteFilter.value == true) list?.filter { recipe ->
                    recipe.isFavorite
                } else list
                filteredRecipes.value = listFavorite
            } else {
                filteredRecipes.value = data.value
            }
        }
    }

    fun onLikeClicked(recipe: Recipe) = repository.like(recipe.id)

    fun onShareClicked(recipe: Recipe) {
        shareRecipeContent.value = recipe.description
    }

   fun toFilterStates(filterState: FilterState): ArrayList<String> {
        var filterStates = ArrayList<String>()
        when {
            filterState.europeanIsActive -> filterStates.add(Recipe.Companion.Categories.European.toString())
            filterState.asianIsActive -> filterStates.add(Recipe.Companion.Categories.Asian.toString())
            filterState.panAsianIsActive -> filterStates.add(Recipe.Companion.Categories.PanAsian.toString())
            filterState.easternIsActive -> filterStates.add(Recipe.Companion.Categories.Eastern.toString())
            filterState.americanIsActive -> filterStates.add(Recipe.Companion.Categories.American.toString())
            filterState.russianIsActive -> filterStates.add(Recipe.Companion.Categories.Russian.toString())
            filterState.mediterraneanIsActive -> filterStates.add(Recipe.Companion.Categories.Mediterranean.toString())
        }
        return filterStates
    }


}