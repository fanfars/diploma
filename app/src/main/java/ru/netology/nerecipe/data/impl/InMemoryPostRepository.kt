package ru.netology.nerecipe.data.impl

import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.data.PostRepository
import ru.netology.nerecipe.dto.Post

class InMemoryPostRepository : PostRepository {

    private var nextId = GENERATED_POSTS_AMOUNT.toLong()

    private val posts
        get() = checkNotNull(data.value) { "Data value should not be null" }

    override val data = MutableLiveData(
        List(GENERATED_POSTS_AMOUNT) { index ->
            Post(
                id = index + 1L,
                author = "Netology",
                content = "Some random content $index",
                published = "07/05/2022",
                likes = 9999,
                shares = 99999,
                views = 150,
                postVideo = null
            )
        }
    )

    override fun like(postId: Long) {
        data.value = posts.map {
            if (it.id != postId) it else it.copy(
                likedByMe = !it.likedByMe,
                likes = if (it.likedByMe) it.likes - 1 else it.likes + 1
            )
        }
    }

    override fun share(postId: Long) {
        data.value = posts.map {
            if (it.id != postId) it else it.copy(
                shares = it.shares + 1
            )
        }
    }

    override fun view(postId: Long) {
        data.value = posts.map {
            if (it.id != postId) it else it.copy(
                views = it.views + 1
            )
        }
    }

    override fun delete(postId: Long) {
        data.value = posts.filterNot { it.id == postId }
    }

    override fun save(post: Post) {
        if (post.id == PostRepository.NEW_POST_ID) insert(post) else update(post)
    }

    private fun update(post: Post) {
        data.value = posts.map {
            if (it.id == post.id) post else it
        }
    }

    private fun insert(post: Post) {
        data.value = listOf(
            post.copy(id = ++nextId)
        ) + posts
    }

    private companion object {
        const val GENERATED_POSTS_AMOUNT = 5
    }

}