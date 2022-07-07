package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.EditStepInteractionListener
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.impl.SharedPrefsRecipeRepository
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.SingleLiveEvent

class EditRecipeViewModel(
    application: Application
) : AndroidViewModel(application), EditStepInteractionListener {

    private val repository = SharedPrefsRecipeRepository(application)

    val data by repository::recipesData
    val navigateToEditRecipeFragment = SingleLiveEvent<Long>()
    val navigateToNewRecipeFragment = SingleLiveEvent<Long>()

    val currentRecipe = MutableLiveData<Recipe>(null)
    val currentSteps = MutableLiveData<List<CookingStep>>(null)
    val currentStep = MutableLiveData<CookingStep>(null)
    val currentStepNumber = MutableLiveData<Int>(null)

    val emptyRecipe = Recipe(
        id = RecipeRepository.NEW_POST_ID,
        title = "",
        author = "",
        description = "",
        category = "",
        steps = listOf(
            CookingStep(stepDescription = "", stepNumber = 1, stepTime = 0)
        )
    )

    fun onAddButtonClicked() {
        val recipe = emptyRecipe
        repository.save(recipe)
        navigateToNewRecipeFragment.value = RecipeRepository.NEW_POST_ID
    }

    fun saveRecipeWithSteps(recipe: Recipe) = repository.save(recipe)

    fun saveStepAfter(step: CookingStep) {
        repository.saveStepAfter(step, currentStepNumber.value!!.toInt())
    }

    fun saveStepBefore(step: CookingStep) {
        repository.saveStepBefore(step, currentStepNumber.value!!.toInt())
    }

    fun removeStepByNumber(stepNumber: Int) {
        currentStepNumber.value = stepNumber
    }

    override fun onStepClicked(cookingStep: CookingStep) {
        currentStep.value = cookingStep
    }

    fun onSaveButtonClicked(recipe: Recipe) {
        if (
            recipe.title.isBlank() ||
            recipe.author.isBlank() ||
            recipe.category.isBlank()
        ) navigateToEditRecipeFragment.value = recipe.id
        else {
            repository.save(recipe)

        }
    }
}