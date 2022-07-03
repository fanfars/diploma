package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.data.impl.FileRecipeRepository
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.SingleLiveEvent
import java.util.*

class FilterRecipeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = FileRecipeRepository(application)

    val data by repository::data
    val navigateToFeedFragment = SingleLiveEvent<Unit>()
    val recipeToDesign = MutableLiveData<Recipe?>(null)
    val filteredRecipes = MediatorLiveData<List<Recipe>>()
    val currentRecipe = MutableLiveData<Recipe?>(null)
    var favoriteFilter = MutableLiveData<Boolean>()
    val categoriesList = MutableLiveData<List<String>>()
    val searchQuery = MutableLiveData<String>()
    val shareRecipeContent = SingleLiveEvent<String>()
    val navigateToRecipeScreen = SingleLiveEvent<Long>()

    init {
        favoriteFilter.value = false

        filteredRecipes.addSource(favoriteFilter) {
            favoriteFilter()
        }
    }

    fun onLikeClicked(recipe: Recipe) = repository.like(recipe.id)

    fun onShareClicked(recipe: Recipe) {
        shareRecipeContent.value = recipe.description
    }


    private fun favoriteFilter() {
        val listFavorite = if (favoriteFilter.value == true) data.value?.filter { recipe ->
            recipe.isFavorite
        } else data.value
        filteredRecipes.value = listFavorite

    }

}