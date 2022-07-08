package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.EditStepInteractionListener
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.data.FilterRepository
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.impl.FileRecipeRepository
import ru.netology.nerecipe.data.impl.SharedPrefsRecipeRepository
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.dto.FilterState
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.SingleLiveEvent
import java.util.*

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener, EditStepInteractionListener {

    private val repository: RecipeRepository = FileRecipeRepository(application)
    val data by repository::data

    private val filterRepository: FilterRepository = SharedPrefsRecipeRepository(application)
    val filterData by filterRepository::categoryData
    val queryData by filterRepository::queryData

    val navigateToEditRecipeFragment = SingleLiveEvent<Long>()
    val navigateToFilterFragment = SingleLiveEvent<Unit>()
    val navigateToRecipeScreen = SingleLiveEvent<Long>()
    val navigateToFeedFragment = SingleLiveEvent<Unit>()
    val navigateToStepEdit = SingleLiveEvent<List<Long>>()

    val filtratedDataLD: MediatorLiveData<List<Recipe>> = MediatorLiveData<List<Recipe>>()
    val searchQueryLD: MutableLiveData<String> = MutableLiveData()
    val filterByCategoryLD = MutableLiveData<List<String>>()

    val currentSteps = MutableLiveData<List<CookingStep>>(null)
    val currentPosition = MutableLiveData<Int>(null)

    val shareRecipeContent = SingleLiveEvent<String>()

    init {
        filterByCategoryLD.value =
            toFilterStates(filterRepository.categoryData.value ?: FilterState())
        searchQueryLD.value = queryData.value
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

    fun onSaveQuery(query: String) {
        filterRepository.saveQuery(query)
    }

    fun onSaveCategories(filterState: FilterState) {
        filterRepository.saveCategories(filterState)
        filterByCategoryLD.value =
            toFilterStates(filterRepository.categoryData.value ?: FilterState())
    }


    // region  RecipeInteractionListener

    override fun onLikeClicked(recipe: Recipe) = repository.like(recipe.id)

    override fun saveRecipeWithSteps(recipe: Recipe) = repository.save(recipe)

    override fun onShareClicked(recipe: Recipe) {
        shareRecipeContent.value = recipe.description
    }

    override fun onRemoveClicked(recipe: Recipe) = repository.delete(recipe.id)

    override fun onEditClicked(recipe: Recipe) {
        navigateToEditRecipeFragment.value = recipe.id
    }

    fun onAddStepClicked(recipeId: Long, position: Int) {
        navigateToStepEdit.value = listOf(recipeId, position.toLong())
    }

    override fun onPostClicked(recipe: Recipe) {
        navigateToRecipeScreen.value = recipe.id
        currentSteps.value =
            filtratedDataLD.value?.first { r -> r.id == recipe.id }?.steps ?: emptyList()
    }

    override fun onStepRemoveClicked(cookingStep: CookingStep) {
        TODO("Not yet implemented")
    }

    override fun onStepEditClicked(cookingStep: CookingStep) {
        TODO("Not yet implemented")
    }

    override fun onFilterClicked() {
        navigateToFilterFragment.call()
    }

    override fun onFavoriteClicked() {
        repository.filterByFavorite()
    }

    override fun onUndoClicked() {
    }

    override fun removeRecipeById(recipeID: Long) {
        repository.delete(recipeID)
        navigateToFeedFragment.call()
    }

    fun findRecipeById(recipeId: Long): Recipe {

        return repository.data.value?.first { recipe -> recipe.id == recipeId }
            ?: throw RuntimeException("Recipe Id not found")
    }

    override fun recipeUp(position: Int) {
        if (position == repository.countOfRecipes()) return else
            repository.moveRecipeToPosition(position, position + 1)
    }

    override fun moveRecipe(from: Int, to: Int) {
        repository.moveRecipeToPosition(from, to)
    }

    override fun recipeDown(position: Int) {
        if (position == 1) return else
            repository.moveRecipeToPosition(position, position - 1)
    }
    // endregion  RecipeInteractionListener

    private fun filterForData() {
        val searchText =
            searchQueryLD.value?.lowercase(Locale.getDefault())?.trim { it <= ' ' } ?: ""

        filtratedDataLD.value = if (searchText.isNotEmpty() && data.value != null) {
            data.value?.filter { recipe ->
                recipe.title.lowercase(Locale.getDefault()).contains(searchText)
            } ?: throw RuntimeException("Empty data.value")
        } else {
            data.value
        }

        if (filterByCategoryLD.value?.size != Recipe.Companion.Categories.values().size) {
            filtratedDataLD.value = filtratedDataLD.value?.filter { recipe ->
                filterByCategoryLD.value?.contains(recipe.category)
                    ?: throw RuntimeException("Empty filtrated data value")
            }
        }
    }

    fun saveStep(recipeId: Long, step: CookingStep) {
        val recipe = findRecipeById(recipeId)
        val steps = recipe.steps
        val newSteps = steps + step
        repository.save(recipe.copy(steps = newSteps))
        currentSteps.value = newSteps
    }

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

//    override fun onStepClicked(cookingStep: CookingStep) {
//
//    }

}