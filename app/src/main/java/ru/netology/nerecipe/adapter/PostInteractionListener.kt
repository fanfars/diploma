package ru.netology.nerecipe.adapter

import ru.netology.nerecipe.dto.Post
import ru.netology.nerecipe.dto.PostVideo

interface PostInteractionListener {

    fun onLikeClicked(post: Post)
    fun onShareClicked(post: Post)
    fun onViewClicked(post: Post)
    fun onRemoveClicked(post: Post)
    fun onEditClicked(post: Post)
    fun onPostClicked(post: Post)
    fun onUndoClicked()
    fun onPlayVideoClicked(postVideo: PostVideo)

}