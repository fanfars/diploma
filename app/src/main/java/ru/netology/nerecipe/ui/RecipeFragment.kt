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
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.FullRecipeFragmentBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.dto.countFormat
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

        binding.stepsButton.setOnClickListener { viewModel.onStepsButtonClicked(singleRecipe) }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val direction = RecipeFragmentDirections.fromRecipeToFeedFragment()
            findNavController().navigate(direction)
        }

        viewModel.data.value?.let { recipes ->
            singleRecipe = recipes.first { recipe -> recipe.id == args.recipeId }
            binding.render(singleRecipe)
        }

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            if (!recipes.any { recipe -> recipe.id == args.recipeId }) {
                return@observe
            }
            if (recipes.isNullOrEmpty()) {
                return@observe
            }
            singleRecipe = recipes.first { recipe -> recipe.id == args.recipeId }
            binding.render(singleRecipe)
        }

//        setFragmentResultListener(requestKey = EditFragment.REQUEST_KEY) { requestKey, bundle ->
//            if (requestKey != EditFragment.REQUEST_KEY) return@setFragmentResultListener
//            val newRecipeContent = bundle.getString(EditFragment.RESULT_KEY)
//                ?: return@setFragmentResultListener
//            viewModel.onSaveButtonClicked(newRecipeContent)
//        }

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

//                cookingTimeCount.setOnClickListener { viewModel.onViewClicked(singleRecipe) }
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

//                recipeCover.setOnClickListener { viewModel.onPlayVideoClicked(singleRecipe.steps!!) }
//                stepsDescription.setOnClickListener { viewModel.onPlayVideoClicked(singleRecipe.steps!!) }

        viewModel.videoPlay.observe(viewLifecycleOwner) { videoLink ->
            val intent = Intent(Intent.ACTION_VIEW).apply {
                val uri = Uri.parse(videoLink)
                data = uri
            }
            val openVideoIntent =
                Intent.createChooser(intent, getString(R.string.chooser_play_video))
            startActivity(openVideoIntent)
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
    author.text = recipe.author
    recipeDescription.text = recipe.description
    category.text = recipe.category
    likesButton.text = recipe.id.toString()
    shareButton.text = recipe.shares.toString()
    cookingTimeCount.text = recipe.cookingTime.toString()
    likesButton.isChecked = recipe.isFavorite

}


