package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nerecipe.databinding.FilterFragmentBinding
import ru.netology.nerecipe.viewModel.FilterRecipeViewModel

class FilterFragment : Fragment() {
    private val filterRecipeViewModel by activityViewModels<FilterRecipeViewModel>()

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

        if (!binding.checkBoxEuropean.isChecked) {
            filterRecipeViewModel.filterState.europeanIsActive = true
        }
        if (!binding.checkBoxAsian.isChecked) {
            filterRecipeViewModel.filterState.asianIsActive = true

        }
        if (!binding.checkBoxPanAsian.isChecked) {
            filterRecipeViewModel.filterState.panAsianIsActive = true

        }
        if (!binding.checkBoxEastern.isChecked) {
            filterRecipeViewModel.filterState.easternIsActive = true
        }
        if (!binding.checkBoxAmerican.isChecked) {
            filterRecipeViewModel.filterState.americanIsActive = true
        }
        if (!binding.checkBoxRussian.isChecked) {
            filterRecipeViewModel.filterState.russianIsActive = true
        }
        if (!binding.checkBoxMediterranean.isChecked) {
            filterRecipeViewModel.filterState.mediterraneanIsActive = true
        }
        if (filterRecipeViewModel.toFilterStates(filterRecipeViewModel.filterState).size == 0) {
            Toast.makeText(activity, "All filters can't be disabled", Toast.LENGTH_LONG).show()
            return
        } else
            saveFilterState(binding)
        findNavController().popBackStack()
    }

    private fun render(binding: FilterFragmentBinding) {

        binding.checkBoxEuropean.isChecked = filterRecipeViewModel.filterState.europeanIsActive
        binding.checkBoxAsian.isChecked = filterRecipeViewModel.filterState.asianIsActive
        binding.checkBoxPanAsian.isChecked = filterRecipeViewModel.filterState.panAsianIsActive
        binding.checkBoxEastern.isChecked = filterRecipeViewModel.filterState.easternIsActive
        binding.checkBoxAmerican.isChecked = filterRecipeViewModel.filterState.americanIsActive
        binding.checkBoxRussian.isChecked = filterRecipeViewModel.filterState.russianIsActive
        binding.checkBoxMediterranean.isChecked =
            filterRecipeViewModel.filterState.mediterraneanIsActive
    }

    private fun saveFilterState(binding: FilterFragmentBinding) {
        with(filterRecipeViewModel) {
            filterState.europeanIsActive = binding.checkBoxEuropean.isChecked
            filterState.asianIsActive = binding.checkBoxAsian.isChecked
            filterState.panAsianIsActive = binding.checkBoxPanAsian.isChecked
            filterState.easternIsActive = binding.checkBoxEastern.isChecked
            filterState.americanIsActive = binding.checkBoxAmerican.isChecked
            filterState.russianIsActive = binding.checkBoxRussian.isChecked
            filterState.mediterraneanIsActive = binding.checkBoxMediterranean.isChecked
        }
    }
}