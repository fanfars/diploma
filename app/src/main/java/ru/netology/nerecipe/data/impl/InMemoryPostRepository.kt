package ru.netology.nerecipe.data.impl

import androidx.lifecycle.MutableLiveData
import ru.netology.nerecipe.data.PostRepository
import ru.netology.nerecipe.dto.Recipe

class InMemoryPostRepository : PostRepository {

    private var nextId = GENERATED_POSTS_AMOUNT.toLong()

    private val posts
        get() = checkNotNull(data.value) { "Data value should not be null" }

    override val data = MutableLiveData(
        List(GENERATED_POSTS_AMOUNT) { index ->
            Recipe(
                id = index + 1L,
                author = "Netology",
                description = "Some random content $index",
                category = "World",
                likes = 9999,
                shares = 99999,
                cookingTime = 150,
                steps = null,
                title = "Title"
            )
        }
    )

    override fun like(postId: Long) {
        data.value = posts.map {
            if (it.id != postId) it else it.copy(
                isFavorite = !it.isFavorite,
                likes = if (it.isFavorite) it.likes - 1 else it.likes + 1
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
                cookingTime = it.cookingTime + 1
            )
        }
    }

    override fun delete(postId: Long) {
        data.value = posts.filterNot { it.id == postId }
    }

    override fun save(recipe: Recipe) {
        if (recipe.id == PostRepository.NEW_POST_ID) insert(recipe) else update(recipe)
    }

    private fun update(recipe: Recipe) {
        data.value = posts.map {
            if (it.id == recipe.id) recipe else it
        }
    }

    private fun insert(recipe: Recipe) {
        data.value = listOf(
            recipe.copy(id = ++nextId)
        ) + posts
    }

    private companion object {
        const val GENERATED_POSTS_AMOUNT = 5
    }

}