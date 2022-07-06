package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.adapter.StepInteractionListener
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.impl.FileRecipeRepository
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.dto.FilterState
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.SingleLiveEvent
import java.util.*

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener, StepInteractionListener {

    private val repository: RecipeRepository = FileRecipeRepository(application)
    val data by repository::data

    val navigateToEditRecipeFragment = SingleLiveEvent<Long>()
    val navigateToFilterFragment = SingleLiveEvent<Unit>()
    val navigateToRecipeScreen = SingleLiveEvent<Long>()
    val navigateToFeedFragment = SingleLiveEvent<Unit>()
    val navigateToRecipeSteps = SingleLiveEvent<Long>()

    val shareRecipeContent = SingleLiveEvent<String>()
    val searchQueryLD: MutableLiveData<String> = MutableLiveData()
    val filterByCategoryLD = MutableLiveData<List<String>>()
    var filterState = FilterState()
    val filtratedDataLD: MediatorLiveData<List<Recipe>> = MediatorLiveData<List<Recipe>>()

    init {
        filterByCategoryLD.value = toFilterStates(filterState)
        filtratedDataLD.addSource(data) { filtratedDataLD.value = data.value }
        filtratedDataLD.addSource(searchQueryLD) { filterForData() }
        filtratedDataLD.addSource(filterByCategoryLD) { filterForData() }

    }


    fun onSaveButtonClicked(recipe: Recipe) {
        if (
            recipe.title.isBlank() ||
            recipe.author.isBlank() ||
            recipe.category.isBlank() ||
            recipe.steps[0].stepDescription.isBlank()
        ) navigateToEditRecipeFragment.value = recipe.id
        else {
            repository.save(recipe)
        }
    }


    // region  RecipeInteractionListener

    override fun onLikeClicked(recipe: Recipe) = repository.like(recipe.id)

    override fun onShareClicked(recipe: Recipe) {
        shareRecipeContent.value = recipe.description
    }

    fun clearFilter() {
        repository.clearFilter()
    }

    override fun onRemoveClicked(recipe: Recipe) = repository.delete(recipe.id)

    override fun onEditClicked(recipe: Recipe) {
        navigateToEditRecipeFragment.value = recipe.id
    }

    override fun onPostClicked(recipe: Recipe) {
        navigateToRecipeScreen.value = recipe.id
    }

    override fun onFilterClicked() {
        navigateToFilterFragment.call()
    }

    override fun onFavoriteClicked() {
        repository.filterByFavorite()
    }

    fun onFilterApply() {
        filterByCategoryLD.value = toFilterStates(filterState)
//        filtratedDataLD.addSource(filterByCategoryLD) {
//            filterByCategory()
//        }
    }

    fun onStepsButtonClicked(recipeId: Long) {
        navigateToRecipeSteps.value = recipeId
    }

    override fun onUndoClicked() {
    }

    override fun onDescriptionLongClicked(cookingStep: CookingStep) {
        TODO("Not yet implemented")
    }

    override fun removeRecipeById(recipeID: Long) {
        repository.delete(recipeID)
        navigateToFeedFragment.call()
    }

    override fun recipeUp(position: Int) {
        if (position == repository.countOfRecipes()) return else
            repository.moveRecipeToPosition(position, position + 1)
    }

    override fun moveRecipe(from: Int, to: Int) {
        repository.moveRecipeToPosition(from, to)
    }

    override fun recipeDown(recipeID: Int) {
        if (recipeID == 1) return else
            repository.moveRecipeToPosition(recipeID, recipeID - 1)
    }

    fun onQueryTextSubmit(queryText: String) {
        searchQueryLD.value = queryText
    }

    private fun filterForData() {

        val searchText =
            searchQueryLD.value?.lowercase(Locale.getDefault())?.trim { it <= ' ' } ?: ""

        filtratedDataLD.value = if (searchText.isNotEmpty() && data.value != null) {
            data.value?.filter { recipe ->
                recipe.title.lowercase(Locale.getDefault()).contains(searchText)
            } ?: throw RuntimeException("Empty data.value")
        } else data.value

        if (filterByCategoryLD.value?.size != Recipe.Companion.Categories.values().size) {
            filtratedDataLD.value?.filter { recipe ->
                filterByCategoryLD.value?.contains(recipe.category)
                    ?: throw RuntimeException("Empty filtrated data value")
            }
        }
    }

    // endregion  RecipeInteractionListener
    companion object {

        fun toFilterStates(filterState: FilterState): List<String> {
            val filterStates = ArrayList<String>()

            if (filterState.europeanIsActive) filterStates.add(Recipe.Companion.Categories.European.toString())
            if (filterState.asianIsActive) filterStates.add(Recipe.Companion.Categories.Asian.toString())
            if (filterState.panAsianIsActive) filterStates.add(Recipe.Companion.Categories.PanAsian.toString())
            if (filterState.easternIsActive) filterStates.add(Recipe.Companion.Categories.Eastern.toString())
            if (filterState.americanIsActive) filterStates.add(Recipe.Companion.Categories.American.toString())
            if (filterState.russianIsActive) filterStates.add(Recipe.Companion.Categories.Russian.toString())
            if (filterState.mediterraneanIsActive) filterStates.add(Recipe.Companion.Categories.Mediterranean.toString())
            return filterStates
        }

    }

}