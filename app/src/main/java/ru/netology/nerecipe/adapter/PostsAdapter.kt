package ru.netology.nerecipe.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.PostFragmentBinding
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.dto.countFormat


internal class PostsAdapter(
    private val interactionListener: PostInteractionListener
) : ListAdapter<Recipe, PostsAdapter.ViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostFragmentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, interactionListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class ViewHolder(
        private val binding: PostFragmentBinding,
        listener: PostInteractionListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var recipe: Recipe

        private val popupMenu by lazy {
            PopupMenu(itemView.context, binding.menu).apply {
                inflate(R.menu.options_post)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            listener.onRemoveClicked(recipe)
                            true
                        }
                        R.id.edit -> {
                            listener.onEditClicked(recipe)
                            true
                        }
                        else -> false
                    }
                }
            }
        }

        init {
            binding.likesButton.setOnClickListener { listener.onLikeClicked(recipe) }
            binding.shareButton.setOnClickListener { listener.onShareClicked(recipe) }
//            binding.cookingTimeCount.setOnClickListener { listener.onViewClicked(recipe) }
            binding.content.setOnClickListener { listener.onPostClicked(recipe) }
            binding.author.setOnClickListener { listener.onPostClicked(recipe) }
            binding.avatar.setOnClickListener { listener.onPostClicked(recipe) }
//            binding.published.setOnClickListener { listener.onPostClicked(post) }
//            binding.videoCover.setOnClickListener { listener.onPlayVideoClicked(post.steps!!)}
//            binding.videoContent.setOnClickListener{ listener.onPlayVideoClicked(post.steps!!)}
            binding.menu.setOnClickListener { popupMenu.show() }
        }

        fun bind(recipe: Recipe) {
            this.recipe = recipe

            with(binding) {
                author.text = recipe.author
                content.text = recipe.description
                category.text = recipe.category
                likesButton.text = countFormat(recipe.likes)
                shareButton.text = countFormat(recipe.shares)
                cookingTimeCount.text = countFormat(recipe.cookingTime)
                likesButton.isChecked = recipe.isFavorite
                stepsDescription.text = R.string.steps_description.toString()
                if (recipe.steps != null) videoGroup.visibility =
                    View.VISIBLE else videoGroup.visibility = View.GONE
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