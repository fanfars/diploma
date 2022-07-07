package ru.netology.nerecipe.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FullRecipeFragmentBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeFragment : Fragment() {

    private val args by navArgs<RecipeFragmentArgs>()

    private val viewModel by activityViewModels<RecipeViewModel>()
    private lateinit var singleRecipe: Recipe


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FullRecipeFragmentBinding.inflate(layoutInflater, container, false)
        .also { binding ->

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                val direction = RecipeFragmentDirections.fromRecipeToFeedFragment()
                findNavController().navigate(direction)
            }

            viewModel.data.value?.let { recipes ->
                singleRecipe = recipes.first { recipe -> recipe.id == args.recipeId }
                binding.render(singleRecipe)
            }

            viewModel.navigateToEditRecipeFragment.observe(viewLifecycleOwner) { editRecipeId ->
                val direction = RecipeFragmentDirections.toEditRecipeFragment(editRecipeId)
                findNavController().navigate(direction)
            }

            viewModel.navigateToRecipeSteps.observe(viewLifecycleOwner) { initialContent ->
                val direction = RecipeFragmentDirections.fromRecipeToStepsFragment(initialContent)
                findNavController().navigate(direction)
            }

            binding.likesButton.setOnClickListener {
                viewModel.onLikeClicked(singleRecipe)
            }

            binding.stepsButton.setOnClickListener { viewModel.onStepsButtonClicked(singleRecipe.id) }

            binding.shareButton.setOnClickListener { viewModel.onShareClicked(singleRecipe) }

            viewModel.shareRecipeContent.observe(viewLifecycleOwner) { recipeContent ->
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, recipeContent)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            val popupMenu by lazy {
                PopupMenu(context, binding.menu).apply {
                    inflate(R.menu.options_recipe)
                    setOnMenuItemClickListener { option ->
                        when (option.itemId) {
                            R.id.remove -> {
                                viewModel.onRemoveClicked(singleRecipe)
                                val direction = RecipeFragmentDirections.fromRecipeToFeedFragment()
                                findNavController().navigate(direction)

                                true
                            }
                            R.id.edit -> {
                                viewModel.onEditClicked(singleRecipe)
                                true
                            }
                            else -> {
                                false
                            }
                        }
                    }
                }
            }
            binding.menu.setOnClickListener {
                popupMenu.show()
            }

        }.root
}

private fun FullRecipeFragmentBinding.render(recipe: Recipe) {
    title.text = recipe.title
    author.text = recipe.author
    recipeDescription.text = recipe.description
    category.text = recipe.category
    likesButton.text = recipe.id.toString()
    cookingTimeCount.text = recipe.cookingTime.toString()
    likesButton.isChecked = recipe.isFavorite

}


