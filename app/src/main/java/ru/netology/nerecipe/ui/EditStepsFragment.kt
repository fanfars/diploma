package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.databinding.EditStepsFragmentBinding
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.viewModel.RecipeViewModel


class EditStepsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setFragmentResultListener(
//            requestKey = RecipeFragment.REQUEST_KEY
//        ) { requestKey, bundle ->
//            if (requestKey != RecipeFragment.REQUEST_KEY) return@setFragmentResultListener
//            val editPos = bundle.getString(RecipeFragment.RESULT_KEY)
//                ?: return@setFragmentResultListener
//            viewModel.currentPosition.value = editPos.toInt()
//        }

    }

    private val args by navArgs<EditStepsFragmentArgs>()

    private val viewModel by viewModels<RecipeViewModel>()

    private lateinit var step: CookingStep

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = EditStepsFragmentBinding.inflate(layoutInflater, container, false)
        .also { binding ->


            val position = args.position
            val recipeId = args.recipeId

            //viewModel.currentPosition.value ?: throw RuntimeException("Position not found")
            if (position != RecipeFragment.NEW_STEP_ID) {
                step =
                    viewModel.data.value?.first { recipe -> recipe.id == recipeId }!!.steps[position]
                binding.stepDescription.setText(step.stepDescription)
                binding.stepTime.setText(step.stepTime)
            } else {
//                step = CookingStep(
//                    stepDescription = "",
//                    stepTime = -1,
//                )
                binding.stepDescription.text.clear()
                binding.stepTime.text.clear()
            }

            binding.saveStepButton.setOnClickListener {
                val stepDescription = binding.stepDescription.getText().toString()
                val stepTime = binding.stepTime.text.toString().toInt()

                if (stepDescription.isNullOrBlank()) {
                    Toast.makeText(activity, "Step description must be filled", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }

                if (stepTime <= 0) {
                    Toast.makeText(activity, "Time must be positive", Toast.LENGTH_LONG)
                        .show()
                    return@setOnClickListener
                }
                step = CookingStep(stepDescription = stepDescription, stepTime = stepTime)
                viewModel.saveStep(recipeId, step)
                val direction = EditStepsFragmentDirections.fromEditStepToRecipeFragment(recipeId)
                findNavController().navigate(direction)
            }


        }.root

}
