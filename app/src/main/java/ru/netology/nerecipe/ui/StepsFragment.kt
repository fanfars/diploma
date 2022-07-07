package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.adapter.RecipeAdapter
import ru.netology.nerecipe.adapter.StepAdapter
import ru.netology.nerecipe.databinding.StepsRecyclerViewFragmentBinding
import ru.netology.nerecipe.viewModel.RecipeViewModel


class StepsFragment : Fragment() {

    private val args by navArgs<StepsFragmentArgs>()

    private val viewModel by viewModels<RecipeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = StepsRecyclerViewFragmentBinding.inflate(layoutInflater, container, false)
        .also { binding ->

            val adapter = StepAdapter()
            binding.stepRecyclerView.adapter = adapter

            viewModel.filtratedDataLD.observe(viewLifecycleOwner) { recipes ->
                val steps = recipes.find { it.id == args.stepsRecipeId }?.steps
                adapter.submitList(steps)
            }

        }.root

}
