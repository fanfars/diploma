package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
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
        recipe = if (args.recipeId != 0L) {
            viewModel.data.value!!.first { recipe -> recipe.id == args.recipeId }
        } else {
            editRecipeViewModel.data.value!!.first { recipe -> recipe.id == args.recipeId }
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
        })

        binding.saveRecipeButton.setOnClickListener {
            onOkButtonClicked(binding)
        }
    }.root

    private fun onOkButtonClicked(binding: EditRecipeFragmentBinding) {
        val stepTime = binding.stepTime.getText().toString()
        val step = CookingStep(
            stepDescription = binding.stepDescription.getText().toString(),
            stepTime = if (stepTime.isNotBlank()) stepTime.toInt() else {
                Toast.makeText(activity, "Please, input step time", Toast.LENGTH_LONG).show()
                return
            },
            stepNumber = recipe.steps[0].stepNumber
        )
        val newRecipe = recipe.copy(
            title = binding.title.getText().toString(),
            author = binding.author.getText().toString(),
            category = recipeCategory,
            description = binding.description.getText().toString(),
            steps = listOf(step)
        )
        if (!emptyFieldWarning(newRecipe)) return
        viewModel.onSaveButtonClicked(newRecipe)
        editRecipeViewModel.data.value = null
        val direction = EditRecipeFragmentDirections.fromNewToFeedFragment()
        findNavController().navigate(direction)
    }


    companion object {
        const val REQUEST_KEY = "requestKey"
        const val RESULT_KEY = "postNewContent"

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
            recipe.steps.isNullOrEmpty() -> {
                Toast.makeText(activity, "Steps must be filled", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    private fun EditRecipeFragmentBinding.render(recipe: Recipe) {
        title.setText(recipe.title)
        author.setText(recipe.author)
        description.setText(recipe.description)
        stepTime.setText(recipe.steps[0].stepTime.toString())
        stepDescription.setText(recipe.steps[0].stepDescription)
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
}