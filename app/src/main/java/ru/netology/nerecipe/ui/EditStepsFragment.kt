package ru.netology.nerecipe.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.databinding.EditStepsFragmentBinding
import ru.netology.nerecipe.dto.CookingStep
import ru.netology.nerecipe.viewModel.RecipeViewModel


class EditStepsFragment : Fragment() {

    private val args by navArgs<EditStepsFragmentArgs>()

    private val viewModel by activityViewModels<RecipeViewModel>()

    private lateinit var step: CookingStep

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
                binding.stepTime.setText(step.stepTime.toString())
            } else {
//                step = CookingStep(
//                    stepDescription = "",
//                    stepTime = -1,
//                )
                binding.stepDescription.text.clear()
                binding.stepTime.text.clear()
            }

            binding.stepPic.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
                val imgPickIntent = Intent.createChooser(intent, "Select Image from...")
                pickStepImgActivityResultLauncher.launch(imgPickIntent)
            }
            var stepImgPath = ""

            viewModel.newStepImg.observe(viewLifecycleOwner) { path ->
                stepImgPath = path ?: EditRecipeFragment.DEFAULT_IMAGE_PATH
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
                step = CookingStep(
                    stepDescription = stepDescription,
                    stepTime = stepTime,
                    stepCover = stepImgPath
                )
                viewModel.saveStep(recipeId, step, position)
                val direction = EditStepsFragmentDirections.fromEditStepToRecipeFragment(recipeId)
                findNavController().navigate(direction)
            }


        }.root

}
