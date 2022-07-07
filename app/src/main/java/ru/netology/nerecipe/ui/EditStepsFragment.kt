package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.adapter.EditStepAdapter
import ru.netology.nerecipe.databinding.EditStepsFragmentBinding
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.viewModel.EditRecipeViewModel
import ru.netology.nerecipe.viewModel.RecipeViewModel


class EditStepsFragment : Fragment() {

    private val args by navArgs<EditStepsFragmentArgs>()

    private val editRecipeViewModel by viewModels<EditRecipeViewModel>()
    private val viewModel by viewModels<RecipeViewModel>()

    private lateinit var recipe: Recipe

       override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = EditStepsFragmentBinding.inflate(layoutInflater, container, false)
        .also { binding ->

            recipe = if (editOrNew()) {
                viewModel.data.value?.first { recipe -> recipe.id == args.recipeId }
                    ?: throw RuntimeException("Empty current recipe")
            } else {
                editRecipeViewModel.data.value
                    ?: throw RuntimeException("Empty current recipe of editRecipeViewModel")
            }

            editRecipeViewModel.currentRecipe.value = recipe
            editRecipeViewModel.currentSteps.value = recipe.steps

            editRecipeViewModel.currentStepNumber.value = recipe.steps[0].stepNumber
            binding.currentStepValue.text = recipe.steps[0].stepNumber.toString()


            val adapter = EditStepAdapter(editRecipeViewModel)
            binding.editStepRecyclerView.adapter = adapter
            editRecipeViewModel.currentSteps.observe(viewLifecycleOwner) { steps ->
                adapter.submitList(steps)
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
                                //viewModel.recipeUp(fromPosition)
                            }
                        } else {
                            for (i in fromPosition downTo toPosition + 1) {
                                //viewModel.recipeDown(fromPosition)
                            }
                        }
                        adapter.notifyItemMoved(fromPosition, toPosition)
                        true
                    }
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.END || direction == ItemTouchHelper.START) {
                        val stepNumber = adapter.currentList[viewHolder.adapterPosition].stepNumber
                        editRecipeViewModel.removeStepByNumber(stepNumber)
                        adapter.notifyItemRemoved(viewHolder.adapterPosition)
                    }
                }
            }

            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(binding.editStepRecyclerView)


            binding.stepAfterButton.setOnClickListener {
                val cookingStep = readStep(binding)
                if (emptyFieldWarning(cookingStep)) {
                    editRecipeViewModel.saveStepAfter(cookingStep)
                    clearStep(binding)
                } else return@setOnClickListener

            }

            binding.stepBeforeButton.setOnClickListener {
                val cookingStep = readStep(binding).copy(stepNumber = BEFORE_STEP)
                if (emptyFieldWarning(cookingStep)) {
                    editRecipeViewModel.saveStepBefore(cookingStep)
                    clearStep(binding)
                } else return@setOnClickListener

            }

            binding.saveRecipe.setOnClickListener {

                viewModel.saveRecipeWithSteps(editRecipeViewModel.data.value!!)

                clearStep(binding)
            }

        }.root

    private fun readStep(binding: EditStepsFragmentBinding): CookingStep {

        return CookingStep(
            stepDescription = binding.stepDescription.getText().toString(),
            stepTime = binding.stepTime.text.toString().toInt(),
            stepNumber = AFTER_STEP,
            cover = null
        )
    }

    private fun clearStep(binding: EditStepsFragmentBinding) {
        binding.stepDescription.text.clear()
        binding.stepTime.text.clear()
    }


    private fun editOrNew(): Boolean {
        return args.recipeId != 0L
    }

    private fun emptyFieldWarning(cookingStep: CookingStep): Boolean {
        return when {
            cookingStep.stepDescription.isNullOrBlank() -> {
                Toast.makeText(activity, "Step description must be filled", Toast.LENGTH_LONG)
                    .show()
                false
            }
            cookingStep.stepTime <= 0 -> {
                Toast.makeText(activity, "Time must be positive", Toast.LENGTH_LONG).show()
                false
            }
            else -> true
        }
    }

    private companion object {
        const val BEFORE_STEP = -1
        const val AFTER_STEP = 0
    }

}
