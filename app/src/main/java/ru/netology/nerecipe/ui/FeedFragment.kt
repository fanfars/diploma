package ru.netology.nerecipe.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.databinding.FeedFragmentBinding
import ru.netology.nerecipe.viewModel.EditRecipeViewModel
import ru.netology.nerecipe.viewModel.FilterRecipeViewModel
import ru.netology.nerecipe.viewModel.RecipeViewModel


class FeedFragment : Fragment() {

    private val viewModel by viewModels<RecipeViewModel>()
    private val editRecipeViewModel by activityViewModels<EditRecipeViewModel>()
    private val filterRecipeViewModel by viewModels<FilterRecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.shareRecipeContent.observe(this) { postContent ->
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, postContent)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(
                intent, getString(R.string.chooser_share_post)
            )
            startActivity(shareIntent)
        }

        editRecipeViewModel.navigateToNewRecipeFragment.observe(this) { newRecipeId ->
            val direction = FeedFragmentDirections.fromFeedFragmentToNewRecipe(newRecipeId)
            findNavController().navigate(direction)
        }

        viewModel.navigateToRecipeScreen.observe(this) { initialRecipeId ->
            val direction = FeedFragmentDirections.toPostFragment(initialRecipeId)
            findNavController().navigate(direction)
        }

        viewModel.navigateToFilterFragment.observe(this) {
            val direction = FeedFragmentDirections.fromFeedFragmentToFilter()
            findNavController().navigate(direction)
        }

        viewModel.navigateToFavoriteFragment.observe(this) {
            val direction = FeedFragmentDirections.fromFeedFragmentToFavoriteRecipes()
            findNavController().navigate(direction)
        }

        viewModel.videoPlay.observe(this) { videoLink ->

            val intent = Intent(Intent.ACTION_VIEW).apply {
                val uri = Uri.parse(videoLink)
                data = uri
            }

            val openVideoIntent =
                Intent.createChooser(intent, getString(R.string.chooser_play_video))
            startActivity(openVideoIntent)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FeedFragmentBinding.inflate(layoutInflater, container, false).also { binding ->

        val adapter = RecipeAdapter(viewModel)
        binding.recipeRecyclerView.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipes ->
            adapter.submitList(recipes)
        }

        viewModel.recipeCardMoveEvent.observe(viewLifecycleOwner) {

        }

        val simpleCallback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.END) {
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
                val fromPositionId = adapter.currentList[viewHolder.adapterPosition].id
//                val toPositionId = adapter.currentList[target.adapterPosition].id
                val fromPosition = adapter.currentList[viewHolder.adapterPosition].id
                val toPosition = adapter.currentList[target.adapterPosition].id
                if (fromPosition < toPosition) {
                    for (i in fromPosition until toPosition) {
                        viewModel.recipeUp(fromPositionId)
                    }
                } else {
                    for (i in fromPosition downTo toPosition + 1) {
                        viewModel.recipeDown(fromPositionId)
                    }
                }
                viewModel.recipeCardMoveEvent.call()
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.END || direction == ItemTouchHelper.START) {
                    val recipeId = adapter.currentList[viewHolder.adapterPosition].id
                    viewModel.removeRecipeById(recipeId)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recipeRecyclerView)

        binding.bottomToolbar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorites -> {
                    filterRecipeViewModel.favoriteFilter.value = true
                    viewModel.onFavoriteClicked()
                    true
                }
                R.id.filter -> {
                    viewModel.onFilterClicked()
                    true
                }
                R.id.add -> {
                    editRecipeViewModel.onAddButtonClicked()
                    true
                }
                else -> false
            }
        }

    }.root

}
