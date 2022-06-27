package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.databinding.RecipeFragmentBinding
import ru.netology.nerecipe.dto.Recipe


internal class RecipeAdapter(
    private val interactionListener: RecipeInteractionListener
) : ListAdapter<Recipe, RecipeAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecipeFragmentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    class ViewHolder(
        private val binding: RecipeFragmentBinding,
        listener: RecipeInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        init {
            binding.likesButton.setOnClickListener { listener.onLikeClicked(recipe) }
            binding.shareButton.setOnClickListener { listener.onShareClicked(recipe) }
            binding.title.setOnClickListener { listener.onPostClicked(recipe) }
            binding.author.setOnClickListener { listener.onPostClicked(recipe) }
            binding.avatar.setOnClickListener { listener.onPostClicked(recipe) }
        }

        fun bind(recipe: Recipe) {
            this.recipe = recipe

            with(binding) {
                title.text = recipe.title
                author.text = recipe.author
                category.text = recipe.category
                likesButton.text = recipe.id.toString()
                shareButton.text = recipe.shares.toString()
                cookingTimeCount.text = recipe.cookingTime.toString()
                likesButton.isChecked = recipe.isFavorite

            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem == newItem
    }


}