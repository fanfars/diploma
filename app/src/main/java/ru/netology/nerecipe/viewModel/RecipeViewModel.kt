package ru.netology.nerecipe.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.RecipeInteractionListener
import ru.netology.nerecipe.adapter.StepInteractionListener
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.impl.FileRecipeRepository
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.SingleLiveEvent

class RecipeViewModel(
    application: Application
) : AndroidViewModel(application), RecipeInteractionListener, StepInteractionListener {

    private val repository: RecipeRepository = FileRecipeRepository(application)

    val data by repository::data
    val navigateToNewRecipeFragment = SingleLiveEvent<Long>()
    val recipeCardMoveEvent = SingleLiveEvent<Unit>()
    val shareRecipeContent = SingleLiveEvent<String>()
    val videoPlay = SingleLiveEvent<String>()
    val navigateToEditRecipeFragment = SingleLiveEvent<Long>()
    val navigateToRecipeScreen = SingleLiveEvent<Long>()
    val navigateToRecipeSteps = SingleLiveEvent<Long>()
    private val navigateToFeedFragment = SingleLiveEvent<Unit>()
    val currentRecipe = MutableLiveData<Recipe?>(null)

    fun onSaveButtonClicked(recipe: Recipe) {
        if (
            recipe.title.isBlank() ||
            recipe.author.isBlank() ||
            recipe.category.isBlank() ||
            recipe.steps.isEmpty()
        ) navigateToEditRecipeFragment.value = recipe.id
        else {
            repository.save(recipe)
            currentRecipe.value = null
        }
    }


//    fun onAddButtonClicked() {
//        val recipe = Recipe(
//            id = RecipeRepository.NEW_POST_ID,
//            title = "",
//            author = "",
//            description = "",
//            category = "",
//            steps = listOf(
//                CookingStep(stepDescription = "", stepNumber = 0, stepTime = 0)
//            )
//        )
//        repository.save(recipe)
//        navigateToNewRecipeFragment.value = repository.getLastId()
//
//    }


    // region  RecipeInteractionListener

    override fun onLikeClicked(recipe: Recipe) = repository.like(recipe.id)

    override fun onShareClicked(recipe: Recipe) {
        shareRecipeContent.value = recipe.description

    }


    override fun onRemoveClicked(recipe: Recipe) = repository.delete(recipe.id)

    override fun onEditClicked(recipe: Recipe) {
        currentRecipe.value = recipe
        navigateToEditRecipeFragment.value = recipe.id
    }

    override fun onPostClicked(recipe: Recipe) {
        navigateToRecipeScreen.value = recipe.id
    }

    fun onStepsButtonClicked(recipe: Recipe) {
        currentRecipe.value = recipe
        navigateToRecipeSteps.value = recipe.id
    }

    override fun onUndoClicked() {
        currentRecipe.value = null
    }

    override fun onDescriptionLongClicked(cookingStep: CookingStep) {
        TODO("Not yet implemented")
    }

    override fun removeRecipeById(recipeID: Long) {
        repository.delete(recipeID)
        Log.d("removeRecipeById", "$recipeID")
        navigateToFeedFragment.call()
    }


    override fun recipeUp(recipeID: Long) {
        if (recipeID == 1L) return else
            repository.moveRecipeToPosition(recipeID, recipeID - 1L)
    }

    override fun moveRecipe(from: Long, to: Long) {
        Log.d("moveRecipe", "$from $to")
        repository.moveRecipeToPosition(from, to)
    }

    override fun recipeDown(recipeID: Long) {
        if (recipeID == repository.countOfRecipes()) return else
            repository.moveRecipeToPosition(recipeID, recipeID + 1L)
    }

// endregion  RecipeInteractionListener
}