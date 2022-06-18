package ru.netology.nerecipe.data

import androidx.lifecycle.LiveData
import ru.netology.nerecipe.dto.Recipe

interface PostRepository {

    val data: LiveData<List<Recipe>>
    fun like(postId: Long)
    fun share(postId: Long)
    fun view(postId: Long)
    fun delete(postId: Long)
    fun save(recipe: Recipe)

    companion object {
        const val NEW_POST_ID = 0L
    }

}