package ru.netology.nerecipe.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private val viewModel by activityViewModels<RecipeViewModel>()
    private val editRecipeViewModel by viewModels<EditRecipeViewModel>()
    private lateinit var recipe: Recipe
    private var recipeCategory = ""


    private val pickStepImgActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.newStepImg.value = uri.toString()
            }
        }

    private val pickRecipeImgActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data!!
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.newRecipeImg.value = uri.toString()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = EditRecipeFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        recipe = if (editOrNew()) {
            binding.stepDescription.visibility = View.GONE
            binding.stepTime.visibility = View.GONE
            binding.stepsDescriptionText.visibility = View.GONE
            binding.stepPic.visibility = View.GONE
            binding.stepTimePic.visibility = View.GONE

            viewModel.data.value!!.first { recipe -> recipe.id == args.recipeId }

        } else {
            editRecipeViewModel.data.value ?: throw RuntimeException("Empty editRecipeData")
        }
        viewModel.newRecipeImg.value = recipe.recipeCover ?: DEFAULT_IMAGE_PATH
        viewModel.currentRecipe.value = recipe

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


        var recipeImgPath = ""

        viewModel.newRecipeImg.observe(viewLifecycleOwner) { path ->
            recipeImgPath = path ?: DEFAULT_IMAGE_PATH
            viewModel.currentRecipe.value =
                viewModel.currentRecipe.value?.copy(recipeCover = path)
        }

        binding.recipePic.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            val imgPickIntent = Intent.createChooser(intent, "Select Image from...")
            pickRecipeImgActivityResultLauncher.launch(imgPickIntent)
        }

        var stepImgPath = ""

        binding.stepPic.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
            }
            val imgPickIntent = Intent.createChooser(intent, "Select Image from...")
            pickStepImgActivityResultLauncher.launch(imgPickIntent)
        }

        viewModel.newStepImg.observe(viewLifecycleOwner) { path ->
            stepImgPath = path ?: DEFAULT_IMAGE_PATH
        }

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
                stepTime = if (stepTime.isNotBlank() && stepTime.isDigitsOnly() && stepTime.toInt() > 0)
                    stepTime.toInt() else {
                    Toast.makeText(activity, "Please, input step time", Toast.LENGTH_LONG).show()
                    INCORRECT_STEP_TIME
                },
                stepCover = viewModel.newStepImg.value ?: DEFAULT_IMAGE_PATH
            )
        }
        val newRecipe = recipe.copy(
            title = binding.title.getText().toString(),
            author = binding.author.getText().toString(),
            category = recipeCategory,
            description = binding.description.getText().toString(),
            steps = mutableListOf(step),
            recipeCover = viewModel.newRecipeImg.value ?: DEFAULT_IMAGE_PATH
        )

        if (!emptyFieldWarning(newRecipe)) return
        newRecipe.cookingTime = newRecipe.steps.sumOf { cookingStep -> cookingStep.stepTime }
        viewModel.onSaveButtonClicked(newRecipe)
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
        recipePic.setImageURI(Uri.parse(recipe.recipeCover ?: DEFAULT_IMAGE_PATH))
    }

    companion object {
        const val INCORRECT_STEP_TIME = -1
        const val FIRST_STEP = 0
        const val DEFAULT_IMAGE_PATH =
            "android.resource://ru.netology.nerecipe/drawable/ic_outline_dining_24"
    }
}