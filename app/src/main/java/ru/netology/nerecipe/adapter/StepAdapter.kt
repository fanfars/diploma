package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.StepFragmentBinding
import ru.netology.nerecipe.dto.CookingStep


internal class StepAdapter(

) : ListAdapter<CookingStep, StepAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StepFragmentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(
        private val binding: StepFragmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var step: CookingStep


        fun bind(step: CookingStep) {
            this.step = step

            with(binding) {
                stepNumber.text = step.stepNumber.toString()
                stepDescription.text = step.stepDescription
                stepTime.text = step.stepTime.toString()
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CookingStep>() {
        override fun areItemsTheSame(oldItem: CookingStep, newItem: CookingStep): Boolean =
            oldItem.stepNumber == newItem.stepNumber

        override fun areContentsTheSame(oldItem: CookingStep, newItem: CookingStep): Boolean =
            oldItem == newItem
    }

}