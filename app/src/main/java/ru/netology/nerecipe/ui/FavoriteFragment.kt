package ru.netology.nerecipe.ui

import android.content.Intent
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


class FavoriteFragment : Fragment() {

    private val filterRecipeViewModel by viewModels<FilterRecipeViewModel>()
    private val editRecipeViewModel by activityViewModels<EditRecipeViewModel>()
    private val viewModel by viewModels<RecipeViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        filterRecipeViewModel.shareRecipeContent.observe(this) { postContent ->
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

        viewModel.navigateToRecipeScreen.observe(viewLifecycleOwner) { initialRecipeId ->
            val direction =
                FavoriteFragmentDirections.fromFavoriteFragmentToRecipeFragment(initialRecipeId)
            findNavController().navigate(direction)
        }

        filterRecipeViewModel.navigateToFeedFragment.observe(viewLifecycleOwner) {
            val direction = FavoriteFragmentDirections.fromFavoriteFragmentToFeed()
            findNavController().navigate(direction)
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
            val favorite = recipes.filter { it.isFavorite }
            adapter.submitList(favorite)
        }




        val simpleCallback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.END) {
            override fun isLongPressDragEnabled(): Boolean {
                return false
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
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.END || direction == ItemTouchHelper.START) {
                    val recipe = adapter.currentList[viewHolder.adapterPosition]
                    filterRecipeViewModel.onLikeClicked(recipe)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recipeRecyclerView)

        binding.bottomToolbar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.Home -> {
                    filterRecipeViewModel.navigateToFeedFragment.call()
                    true
                }
//                R.id.filter -> {
//                    viewModel.filterFragment.call()
//                    true
//                }
                R.id.add -> {
                    editRecipeViewModel.onAddButtonClicked()
                    true
                }
                else -> false
            }
        }

    }.root

}
