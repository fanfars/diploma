package ru.netology.nerecipe.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.databinding.FeedFragmentBinding
import ru.netology.nerecipe.util.hideKeyboard
import ru.netology.nerecipe.util.showKeyboard
import ru.netology.nerecipe.viewModel.EditRecipeViewModel
import ru.netology.nerecipe.viewModel.RecipeViewModel


class FeedFragment : Fragment() {

    private val viewModel by activityViewModels<RecipeViewModel>()
    private val editRecipeViewModel by activityViewModels<EditRecipeViewModel>()

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
            val direction = FeedFragmentDirections.toRecipeFragment(initialRecipeId)
            findNavController().navigate(direction)
        }

        viewModel.navigateToFilterFragment.observe(this) {
            val direction = FeedFragmentDirections.fromFeedFragmentToFilter()
            findNavController().navigate(direction)
        }

        viewModel.navigateToFeedFragment.observe(this) {
            val direction = FeedFragmentDirections.toFeedFragment()
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

        viewModel.filtratedDataLD.observe(viewLifecycleOwner) { recipes ->
            if (recipes.isEmpty()) binding.emptyPic.visibility = View.VISIBLE else
                binding.emptyPic.visibility = View.GONE
            adapter.submitList(recipes)
        }

        binding.searchBar.visibility = View.GONE

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
                return if (viewHolder.itemViewType != target.itemViewType) false else {
                    val fromPosition = viewHolder.adapterPosition
                    val toPosition = target.adapterPosition
                    if (fromPosition < toPosition) {
                        for (i in fromPosition until toPosition) {
                            viewModel.recipeUp(fromPosition)
                        }
                    } else {
                        for (i in fromPosition downTo toPosition + 1) {
                            viewModel.recipeDown(fromPosition)
                        }
                    }
                    adapter.notifyItemMoved(fromPosition, toPosition)
                    true
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.END || direction == ItemTouchHelper.START) {
                    val recipeId = adapter.currentList[viewHolder.adapterPosition].id
                    viewModel.removeRecipeById(recipeId)
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }
        }

        fun setUpSearchView() {
            binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.searchBar.hideKeyboard()
                    if (!query.isNullOrBlank()) viewModel.onSaveQuery(query) else return false
                    viewModel.navigateToFeedFragment.call()
                    return true

                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText.isNullOrEmpty()) {
                        adapter.submitList(viewModel.filtratedDataLD.value)
                        return true
                    }
                    var list = adapter.currentList
                    list = list.filter { recipe ->
                        recipe.title.lowercase().contains(newText.lowercase())
                    }
                    adapter.submitList(list)
                    return true
                }
            })
        }

        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.recipeRecyclerView)
        binding.bottomToolbar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> {
                    with(viewModel) {
                        navigateToFeedFragment.call()
                        onSaveQuery("")
                        filterByFavLD.value = false
                    }
                    true
                }
                R.id.favorites -> {
                    viewModel.onFavoriteClicked()
                    binding.searchBar.visibility = View.GONE
                    true
                }
                R.id.search -> {
                    with(binding.searchBar) {
                        visibility = View.VISIBLE
                        onActionViewExpanded()
                        requestFocus()
                        showKeyboard()
                    }
                    setUpSearchView()
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

