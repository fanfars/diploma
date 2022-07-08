package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.EditRecipeFragmentBinding
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.EditRecipeViewModel
import ru.netology.nerecipe.viewModel.RecipeViewModel


class EditRecipeFragment : Fragment() {

    private val args by navArgs<EditRecipeFragmentArgs>()
    private val viewModel by viewModels<RecipeViewModel>()
    private val editRecipeViewModel by viewModels<EditRecipeViewModel>()
    private lateinit var recipe: Recipe
    private var recipeCategory = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = EditRecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        recipe = if (editOrNew()) {
            binding.stepDescription.visibility = View.GONE
            binding.stepTime.visibility = View.GONE
            binding.stepsDescriptionText.visibility = View.GONE

            viewModel.data.value!!.first { recipe -> recipe.id == args.recipeId }

        } else {
            editRecipeViewModel.data.value ?: throw RuntimeException("Empty editRecipeData")
        }
        binding.render(recipe)

        val spinner = binding.categorySpinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View?,
                position: Int,
                id: Long
            ) {
                recipeCategory = parent.getItemAtPosition(position) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        )


        binding.saveRecipeButton.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked(binding: EditRecipeFragmentBinding) {
        val stepTime = binding.stepTime.getText().toString()
        val step = if (editOrNew()) {
            recipe.steps[FIRST_STEP]
        } else {
            CookingStep(
                stepDescription = binding.stepDescription.getText().toString(),
                stepTime = if (stepTime.isNotBlank() && stepTime.isDigitsOnly() && stepTime.toInt() > 0) stepTime.toInt() else {
                    Toast.makeText(activity, "Please, input step time", Toast.LENGTH_LONG).show()
                    INCORRECT_STEP_TIME
                }
            )
        }
        val newRecipe = recipe.copy(
            title = binding.title.getText().toString(),
            author = binding.author.getText().toString(),
            category = recipeCategory,
            description = binding.description.getText().toString(),
            steps = listOf(step)
        )

        if (!emptyFieldWarning(newRecipe)) return
        newRecipe.cookingTime = newRecipe.steps.sumOf { cookingStep -> cookingStep.stepTime }
//        if (editOrNew()) {
        viewModel.onSaveButtonClicked(newRecipe)
//        } else
//            editRecipeViewModel.onSaveButtonClicked(newRecipe)
        //editRecipeViewModel.data.value = null
        val direction = EditRecipeFragmentDirections.fromNewToFeedFragment()
        findNavController().navigate(direction)
    }


    private fun emptyFieldWarning(recipe: Recipe): Boolean {
        return when {
            recipe.title.isNullOrBlank() -> {
                Toast.makeText(activity, "Title must be filled", Toast.LENGTH_LONG).show()
                false
            }
            recipe.author.isNullOrBlank() -> {
                Toast.makeText(activity, "Author must be filled", Toast.LENGTH_LONG).show()
                false
            }
            recipe.category.isNullOrBlank() -> {
                Toast.makeText(activity, "Category must be filled", Toast.LENGTH_LONG).show()
                false
            }
            recipe.steps[0].stepDescription.isNullOrBlank() || recipe.steps[0].stepTime <= 0 -> {
                Toast.makeText(activity, "Check cooking step values", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    private fun editOrNew(): Boolean {
        return args.recipeId != 0L
    }

    private fun EditRecipeFragmentBinding.render(recipe: Recipe) {
        title.setText(recipe.title)
        author.setText(recipe.author)
        description.setText(recipe.description)
        categorySpinner.setSelection(
            when (recipe.category) {
                Recipe.Companion.Categories.European.toString() -> 1
                Recipe.Companion.Categories.Asian.toString() -> 2
                Recipe.Companion.Categories.PanAsian.toString() -> 3
                Recipe.Companion.Categories.Eastern.toString() -> 4
                Recipe.Companion.Categories.American.toString() -> 5
                Recipe.Companion.Categories.Russian.toString() -> 6
                Recipe.Companion.Categories.Mediterranean.toString() -> 7
                else -> 1
            }
        )
    }

    companion object {
        const val INCORRECT_STEP_TIME = -1
        const val FIRST_STEP = 0
    }
}