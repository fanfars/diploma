package ru.netology.nerecipe.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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
import ru.netology.nerecipe.ui.RecipeFragment
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
    val navigateToStepEdit = SingleLiveEvent<List<Long>>()
    val navigateToRecipeScreen = SingleLiveEvent<Long>()
    val navigateToFeedFragment = SingleLiveEvent<Unit>()

    val filtratedDataLD: MediatorLiveData<List<Recipe>> = MediatorLiveData<List<Recipe>>()
    val searchQueryLD: MutableLiveData<String> = MutableLiveData()
    val filterByCategoryLD = MutableLiveData<List<String>>()


    val currentSteps = MutableLiveData<MutableList<CookingStep>>()
    val currentRecipe = SingleLiveEvent<Recipe>()
    val newRecipeImg = MutableLiveData<String?>()
    val newStepImg = MutableLiveData<String?>()

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
        newRecipeImg.value = null
        newStepImg.value = null
    }

    fun getSteps(recipeId: Long) {
        currentSteps.value = findRecipeById(recipeId).steps
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
            filtratedDataLD.value?.first { r -> r.id == recipe.id }?.steps ?: mutableListOf()
    }

    override fun onStepRemoveClicked(step: CookingStep) {
        val recipe = currentRecipe.value ?: throw RuntimeException("Current id not found")

        val steps = recipe.steps
        if (steps.size > 1) {
            steps.remove(step)
            repository.save(recipe.copy(steps = steps))
            currentRecipe.value = currentRecipe.value?.copy(steps = steps)
            currentSteps.value = steps
        } else
            currentSteps.value = steps

    }

    override fun onStepEditClicked(step: CookingStep) {
        val recipe = findRecipeById(
            currentRecipe.value?.id ?: throw RuntimeException("Current id not found")
        )
        val steps = recipe.steps
        val position = steps.indexOf(step)
        navigateToStepEdit.value = listOf(recipe.id, position.toLong())
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

        return data.value?.first { recipe -> recipe.id == recipeId }
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

    fun saveStep(recipeId: Long, step: CookingStep, position: Int) {
        val recipe = findRecipeById(recipeId)
        var steps = recipe.steps
        if (position == RecipeFragment.NEW_STEP_ID) {
            steps.add(step)

        } else {
            steps[position] = step
        }
        repository.save(recipe.copy(steps = steps))
        currentSteps.value = steps
        newStepImg.value = null
    }


    override fun stepUp(position: Int) {
        var steps = currentSteps.value
        if (steps != null) {
            if (position == steps?.size) return else
                Collections.swap(
                    steps,
                    position + 1,
                    position
                )
            currentSteps.value = steps!!
            var recipe = currentRecipe.value
            recipe = recipe!!.copy(steps = steps)
            saveRecipeWithSteps(recipe)
        } else return
    }

    override fun stepDown(position: Int) {
        var steps = currentSteps.value
        if (steps != null) {
            if (position == 0) return else
                Collections.swap(
                    steps,
                    position - 1,
                    position
                )
            currentSteps.value = steps!!
            var recipe = currentRecipe.value
            recipe = recipe!!.copy(steps = steps)
            saveRecipeWithSteps(recipe)
        } else return
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


}