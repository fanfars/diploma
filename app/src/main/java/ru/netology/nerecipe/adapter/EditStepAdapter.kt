package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.StepFragmentBinding
import ru.netology.nerecipe.dto.CookingStep


internal class EditStepAdapter(
    private val interactionListener: EditStepInteractionListener
) : ListAdapter<CookingStep, EditStepAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StepFragmentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(
        private val binding: StepFragmentBinding,
        listener: EditStepInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var step: CookingStep

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.stepMenu).apply {
                inflate(R.menu.options_step)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            listener.onStepRemoveClicked(step)
                            true
                        }
                        R.id.edit -> {
                            listener.onStepEditClicked(step)
                            true
                        }
                        else -> false
                    }
                }
            }
        }


        init {
//            binding.stepDescription.setOnClickListener { listener.onStepClicked(step) }
//            binding.stepPic.setOnClickListener { listener.onStepClicked(step) }
//            binding.stepTime.setOnClickListener { listener.onStepClicked(step) }
            binding.stepMenu.setOnClickListener { popupMenu.show()}
            }

        fun bind(step: CookingStep) {
            this.step = step

            with(binding) {
                stepDescription.text = step.stepDescription
                stepTime.text = step.stepTime.toString()
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<CookingStep>() {
        override fun areItemsTheSame(oldItem: CookingStep, newItem: CookingStep): Boolean =
            oldItem.stepDescription == newItem.stepDescription

        override fun areContentsTheSame(oldItem: CookingStep, newItem: CookingStep): Boolean =
            oldItem == newItem
    }

}