package ru.netology.nerecipe.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.adapter.PostInteractionListener
import ru.netology.nerecipe.data.PostRepository
import ru.netology.nerecipe.data.impl.FilePostRepository
import ru.netology.nerecipe.dto.Post
import ru.netology.nerecipe.dto.PostVideo
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

    val currentPost = MutableLiveData<Post?>(null)

    fun onSaveButtonClicked(content: String) {
        if (content.isBlank()) return
        val post = currentPost.value?.copy(
            content = content
        ) ?: Post(
            id = PostRepository.NEW_POST_ID,
            author = "Me",
            content = content,
            published = "Today",
            postVideo = PostVideo(
                url = "https://www.youtube.com/watch?v=xOgT2qYAzds",
                title = "Онлайн-образование. Революция уже наступила?"
            )
        )
        repository.save(post)
        currentPost.value = null
    }

    fun onAddButtonClicked() {
        navigateToPostContentScreenEvent.call()
    }


    // region  PostInteractionListener

    override fun onLikeClicked(post: Post) = repository.like(post.id)

    override fun onShareClicked(post: Post) {
        sharePostContent.value = post.content

    }

    override fun onViewClicked(post: Post) = repository.view(post.id)

    override fun onRemoveClicked(post: Post) = repository.delete(post.id)

    override fun onEditClicked(post: Post) {
        currentPost.value = post
        navigateToPostContentScreenEvent.value = post.content
    }

    override fun onPostClicked(post: Post) {
        navigateToPostScreenEvent.value = post.id
    }

    override fun onUndoClicked() {
        currentPost.value = null
    }

    override fun onPlayVideoClicked(postVideo: PostVideo) {
        videoPlay.value = postVideo.url!!
    }

// endregion  PostInteractionListener
}