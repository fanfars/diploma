package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.databinding.FilterFragmentBinding
import ru.netology.nerecipe.dto.FilterState
import ru.netology.nerecipe.viewModel.RecipeViewModel
import ru.netology.nerecipe.viewModel.RecipeViewModel.Companion.toFilterStates

class FilterFragment : Fragment() {
    private val viewModel by activityViewModels<RecipeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FilterFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
        render(binding)

        binding.applyButton.setOnClickListener {
            onApplyClicked(binding)
        }
    }.root


    private fun onApplyClicked(binding: FilterFragmentBinding) {
        if (!saveFilterState(binding)) return
        val direction = FilterFragmentDirections.fromFilterFragmentToFeed()
        findNavController().navigate(direction)
    }

    private fun render(binding: FilterFragmentBinding) {
        val filterState: FilterState = viewModel.filterData.value ?: FilterState()
        binding.checkBoxEuropean.isChecked = filterState.europeanIsActive
        binding.checkBoxAsian.isChecked = filterState.asianIsActive
        binding.checkBoxPanAsian.isChecked = filterState.panAsianIsActive
        binding.checkBoxEastern.isChecked = filterState.easternIsActive
        binding.checkBoxAmerican.isChecked = filterState.americanIsActive
        binding.checkBoxRussian.isChecked = filterState.russianIsActive
        binding.checkBoxMediterranean.isChecked = filterState.mediterraneanIsActive
    }

    private fun saveFilterState(binding: FilterFragmentBinding): Boolean {
        val filterState = FilterState()
        with(filterState) {
            europeanIsActive = binding.checkBoxEuropean.isChecked
            asianIsActive = binding.checkBoxAsian.isChecked
            panAsianIsActive = binding.checkBoxPanAsian.isChecked
            easternIsActive = binding.checkBoxEastern.isChecked
            americanIsActive = binding.checkBoxAmerican.isChecked
            russianIsActive = binding.checkBoxRussian.isChecked
            mediterraneanIsActive = binding.checkBoxMediterranean.isChecked
            return if (toFilterStates(filterState).isEmpty()) {
                Toast.makeText(activity, "All filters can't be disabled", Toast.LENGTH_LONG).show()
                false
            } else {
                viewModel.onSaveCategories(filterState)
                true
            }

        }
    }
}