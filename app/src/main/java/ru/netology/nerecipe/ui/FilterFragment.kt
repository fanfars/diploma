package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.databinding.FilterFragmentBinding
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

        binding.applyFilterBtn.setOnClickListener {
            onApplyBtnClicked(binding)
        }
    }.root


    private fun onApplyBtnClicked(binding: FilterFragmentBinding) {
        if (!saveFilterState(binding)) return
        viewModel.onFilterApply()
        val direction = FilterFragmentDirections.fromFilterFragmentToFeed()
        findNavController().navigate(direction)
    }

    private fun render(binding: FilterFragmentBinding) {

        binding.checkBoxEuropean.isChecked = viewModel.filterState.europeanIsActive
        binding.checkBoxAsian.isChecked = viewModel.filterState.asianIsActive
        binding.checkBoxPanAsian.isChecked = viewModel.filterState.panAsianIsActive
        binding.checkBoxEastern.isChecked = viewModel.filterState.easternIsActive
        binding.checkBoxAmerican.isChecked = viewModel.filterState.americanIsActive
        binding.checkBoxRussian.isChecked = viewModel.filterState.russianIsActive
        binding.checkBoxMediterranean.isChecked =
            viewModel.filterState.mediterraneanIsActive
    }

    private fun saveFilterState(binding: FilterFragmentBinding): Boolean {
        with(viewModel) {
            filterState.europeanIsActive = binding.checkBoxEuropean.isChecked
            filterState.asianIsActive = binding.checkBoxAsian.isChecked
            filterState.panAsianIsActive = binding.checkBoxPanAsian.isChecked
            filterState.easternIsActive = binding.checkBoxEastern.isChecked
            filterState.americanIsActive = binding.checkBoxAmerican.isChecked
            filterState.russianIsActive = binding.checkBoxRussian.isChecked
            filterState.mediterraneanIsActive = binding.checkBoxMediterranean.isChecked
            return if (toFilterStates(filterState).size == 0) {
                Toast.makeText(activity, "All filters can't be disabled", Toast.LENGTH_LONG).show()
                false
            } else {
                viewModel.filterByCategoryLD.value = toFilterStates(filterState)
                true
            }

        }
    }
}