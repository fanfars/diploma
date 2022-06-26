package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.adapter.StepAdapter
import ru.netology.nerecipe.databinding.StepsRecyclerViewFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel


class StepsFragment : Fragment() {

    private val args by navArgs<RecipeFragmentArgs>()

    private val viewModel by viewModels<RecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setFragmentResultListener(
//            requestKey = RecipeContentFragment.REQUEST_KEY
//        ) { requestKey, bundle ->
//            if (requestKey != RecipeContentFragment.REQUEST_KEY) return@setFragmentResultListener
//            val newPostContent = bundle.getString(RecipeContentFragment.RESULT_KEY)
//                ?: return@setFragmentResultListener
//            viewModel.onSaveButtonClicked(newPostContent)
//
//        }

//        viewModel.navigateToRecipeScreenEvent.observe(this) { initialRecipeId ->
//            val direction = FeedFragmentDirections.toPostFragment(initialRecipeId)
//            findNavController().navigate(direction)
//        }
//
//        viewModel.videoPlay.observe(this) { videoLink ->
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                val uri = Uri.parse(videoLink)
//                data = uri
//            }
//            val openVideoIntent =
//                Intent.createChooser(intent, getString(R.string.chooser_play_video))
//            startActivity(openVideoIntent)
//        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = StepsRecyclerViewFragmentBinding.inflate(layoutInflater, container, false)
        .also { binding ->

        val adapter = StepAdapter(viewModel)
        binding.stepRecyclerView.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { recipe ->
            adapter.submitList(
                recipe
                    .find { it.id == args.recipeId }
                    ?.steps
            )
        }

        val simpleCallback = object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.END) {
            override fun isLongPressDragEnabled(): Boolean {
                return true
            }
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                viewModel.moveRecipe(fromPosition.toLong() + 1L, toPosition.toLong() + 1L)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == ItemTouchHelper.END) {
                    val position = viewHolder.adapterPosition
                    viewModel.removeRecipeById(position.toLong() + 1L)
                    adapter.notifyItemRemoved(position)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(binding.stepRecyclerView)


    }.root

}
