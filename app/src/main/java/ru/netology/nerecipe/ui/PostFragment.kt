package ru.netology.nerecipe.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nerecipe.R
import ru.netology.nerecipe.databinding.PostFragmentBinding
import ru.netology.nerecipe.dto.Post
import ru.netology.nerecipe.dto.countFormat
import ru.netology.nerecipe.viewModel.PostViewModel

class PostFragment : Fragment() {

    private val args by navArgs<PostFragmentArgs>()

    private val viewModel by viewModels<PostViewModel>()

    private lateinit var singlePost: Post

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            val direction = PostFragmentDirections.fromPostToFeedFragment()
            findNavController().navigate(direction)
        }
        return PostFragmentBinding.inflate(layoutInflater, container, false).also { binding ->
            with(binding) {
                viewModel.data.value?.let { posts ->
                    singlePost = posts.first { post -> post.id == args.postId }
                    render(singlePost)
                }
                viewModel.data.observe(viewLifecycleOwner) { posts ->
                    if (!posts.any { post -> post.id == args.postId }) {
                        return@observe
                    }
                    if (posts.isNullOrEmpty()) {
                        return@observe
                    }
                    singlePost = posts.first { post -> post.id == args.postId }
                    render(singlePost)
                }
                setFragmentResultListener(requestKey = PostContentFragment.REQUEST_KEY) { requestKey, bundle ->
                    if (requestKey != PostContentFragment.REQUEST_KEY) return@setFragmentResultListener
                    val newPostContent = bundle.getString(PostContentFragment.RESULT_KEY)
                        ?: return@setFragmentResultListener
                    viewModel.onSaveButtonClicked(newPostContent)
                }
                viewModel.navigateToPostContentScreenEvent.observe(viewLifecycleOwner) { initialContent ->
                    val direction = PostFragmentDirections.toPostContentFragment(initialContent)
                    findNavController().navigate(direction)
                }
                likesButton.setOnClickListener {
                    viewModel.onLikeClicked(singlePost)
                }

                viewsButton.setOnClickListener { viewModel.onViewClicked(singlePost) }
                shareButton.setOnClickListener { viewModel.onShareClicked(singlePost) }

                viewModel.sharePostContent.observe(viewLifecycleOwner) { postContent ->
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, postContent)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                }

                videoCover.setOnClickListener { viewModel.onPlayVideoClicked(singlePost.postVideo!!) }
                videoContent.setOnClickListener { viewModel.onPlayVideoClicked(singlePost.postVideo!!) }

                viewModel.videoPlay.observe(viewLifecycleOwner) { videoLink ->
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        val uri = Uri.parse(videoLink)
                        data = uri
                    }
                    val openVideoIntent =
                        Intent.createChooser(intent, getString(R.string.chooser_play_video))
                    startActivity(openVideoIntent)
                }

                val popupMenu by lazy {
                    PopupMenu(context, binding.menu).apply {
                        inflate(R.menu.options_post)
                        setOnMenuItemClickListener { option ->
                            when (option.itemId) {
                                R.id.remove -> {
                                    viewModel.onRemoveClicked(singlePost)
                                    val direction = PostFragmentDirections.fromPostToFeedFragment()
                                    findNavController().navigate(direction)

                                    true
                                }
                                R.id.edit -> {
                                    viewModel.onEditClicked(singlePost)
                                    true
                                }
                                else -> {
                                    false
                                }
                            }
                        }
                    }
                }
                binding.menu.setOnClickListener {
                    popupMenu.show()
                }
            }
        }.root
    }

    private fun PostFragmentBinding.render(post: Post) {
        author.text = post.author
        content.text = post.content
        published.text = post.published
        likesButton.text = countFormat(post.likes)
        shareButton.text = countFormat(post.shares)
        viewsButton.text = countFormat(post.views)
        likesButton.isChecked = post.likedByMe
        videoContent.text = post.postVideo?.title
        if (post.postVideo != null) videoGroup.visibility =
            View.VISIBLE else videoGroup.visibility = View.GONE
    }


}