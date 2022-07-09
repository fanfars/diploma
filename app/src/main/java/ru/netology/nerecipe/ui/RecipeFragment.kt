package ru.netology.nerecipe.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.EditStepAdapter
import ru.netology.nerecipe.databinding.FullRecipeFragmentBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.RecipeViewModel

class RecipeFragment : Fragment() {

    private val args by navArgs<RecipeFragmentArgs>()

    private val viewModel by activityViewModels<RecipeViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FullRecipeFragmentBinding.inflate(layoutInflater, container, false)
        .also { binding ->

            requireActivity().onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
                val direction = RecipeFragmentDirections.fromRecipeToFeedFragment()
                findNavController().navigate(direction)
            }

            viewModel.getSteps(args.recipeId)
            viewModel.currentRecipe.value = viewModel.data.value?.first { it.id == args.recipeId }

            val adapter = EditStepAdapter(viewModel)
            binding.stepRecyclerView.adapter = adapter
            viewModel.currentSteps.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            val singleRecipe = viewModel.data.value?.first { recipe ->
                recipe.id == args.recipeId
            } ?: throw RuntimeException("Recipe not found")

            binding.render(singleRecipe)

            viewModel.navigateToEditRecipeFragment.observe(viewLifecycleOwner) { editRecipeId ->
                val direction = RecipeFragmentDirections.toEditRecipeFragment(editRecipeId)
                findNavController().navigate(direction)
            }

            viewModel.navigateToStepEdit.observe(viewLifecycleOwner) { list ->
                val direction = RecipeFragmentDirections.toEditStepFragment(
                    list[RECIPE_ID],
                    list[STEP_POSITION].toInt()
                )
                findNavController().navigate(direction)
            }

            binding.likesButton.setOnClickListener { viewModel.onLikeClicked(singleRecipe) }

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
                                val direction =
                                    RecipeFragmentDirections.fromRecipeToFeedFragment()
                                findNavController().navigate(direction)
                                true
                            }
                            R.id.edit -> {
                                viewModel.onEditClicked(singleRecipe)
                                true
                            }
                            R.id.add_step -> {
                                viewModel.onAddStepClicked(singleRecipe.id, NEW_STEP_ID)
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


            val simpleCallback = object :
                ItemTouchHelper.SimpleCallback(
                    ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.END
                ) {

                override fun isLongPressDragEnabled(): Boolean {
                    return true
                }

                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                    return makeMovementFlags(dragFlags, swipeFlags)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return if (viewHolder.itemViewType != target.itemViewType) false else {
                        val fromPosition = viewHolder.adapterPosition
                        val toPosition = target.adapterPosition
                        if (fromPosition < toPosition) {
                            for (i in fromPosition until toPosition) {
                                viewModel.stepUp(fromPosition)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                viewModel.stepDown(fromPosition)
                            }
                        }
                        adapter.notifyItemMoved(fromPosition, toPosition)
                        true
                    }
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.END || direction == ItemTouchHelper.START) {
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        val step = adapter.currentList.get(viewHolder.layoutPosition)
                        viewModel.onStepRemoveClicked(step)
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(binding.stepRecyclerView)


        }.root

    companion object {
        const val NEW_STEP_ID = -1
        const val RECIPE_ID = 0
        const val STEP_POSITION = 1

    }

}

private fun FullRecipeFragmentBinding.render(recipe: Recipe) {
    title.text = recipe.title
    author.text = recipe.author
    recipeDescription.text = recipe.description
    category.text = recipe.category
    likesButton.text = recipe.id.toString()
    cookingTimeCount.text = recipe.cookingTime.toString()
    likesButton.isChecked = recipe.isFavorite
    recipePic.setImageURI(Uri.parse(recipe.recipeCover))

}


