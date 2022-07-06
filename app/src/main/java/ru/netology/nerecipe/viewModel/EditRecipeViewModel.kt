package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.data.RecipeRepository
import ru.netology.nerecipe.data.impl.SharedPrefsRecipeRepository
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.util.SingleLiveEvent

class EditRecipeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = SharedPrefsRecipeRepository(application)

    val data by repository::data
    val navigateToRecipeSteps = SingleLiveEvent<Long>()
    val navigateToNewRecipeFragment = SingleLiveEvent<Long>()
//    private val navigateToFeedFragment = SingleLiveEvent<Unit>()
    val recipeToDesign = MutableLiveData<Recipe?>(null)
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

    fun onStepsButtonClicked(recipe: Recipe) {
        recipeToDesign.value = recipe
        navigateToRecipeSteps.value = recipe.id
    }

}