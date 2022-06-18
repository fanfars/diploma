package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.PostInteractionListener
import ru.netology.nerecipe.data.PostRepository
import ru.netology.nerecipe.data.impl.FilePostRepository
import ru.netology.nerecipe.dto.Recipe
import ru.netology.nerecipe.dto.CookingSteps
import ru.netology.nerecipe.util.SingleLiveEvent

class PostViewModel(
    application: Application
) : AndroidViewModel(application), PostInteractionListener {

    private val repository: PostRepository = FilePostRepository(application)

    val data by repository::data

    val sharePostContent = SingleLiveEvent<String>()
    val videoPlay = SingleLiveEvent<String>()
    val navigateToPostContentScreenEvent = SingleLiveEvent<String>()
    val navigateToPostScreenEvent = SingleLiveEvent<Long>()

    val currentPost = MutableLiveData<Recipe?>(null)

    fun onSaveButtonClicked(content: String) {
        if (content.isBlank()) return
        val post = currentPost.value?.copy(
            description = content
        ) ?: Recipe(
            id = PostRepository.NEW_POST_ID,
            author = "Me",
            description = content,
            category = "World",
            steps = listOf(
                CookingSteps(stepDescription = "step 1", stepNumber = 1, stepTime = 11),
                CookingSteps(stepDescription = "step 2", stepNumber = 2, stepTime = 22),
                CookingSteps(stepDescription = "step 3", stepNumber = 3, stepTime = 33)
            ),
            title = "Title"
        )
        repository.save(post)
        currentPost.value = null
    }

    fun onAddButtonClicked() {
        navigateToPostContentScreenEvent.call()
    }


    // region  PostInteractionListener

    override fun onLikeClicked(recipe: Recipe) = repository.like(recipe.id)

    override fun onShareClicked(recipe: Recipe) {
        sharePostContent.value = recipe.description

    }

    override fun onViewClicked(recipe: Recipe) = repository.view(recipe.id)

    override fun onRemoveClicked(recipe: Recipe) = repository.delete(recipe.id)

    override fun onEditClicked(recipe: Recipe) {
        currentPost.value = recipe
        navigateToPostContentScreenEvent.value = recipe.description
    }

    override fun onPostClicked(recipe: Recipe) {
        navigateToPostScreenEvent.value = recipe.id
    }

    override fun onUndoClicked() {
        currentPost.value = null
    }

//    override fun onPlayVideoClicked(cookingSteps: CookingSteps) {
//        videoPlay.value = cookingSteps.url!!
//    }

// endregion  PostInteractionListener
}