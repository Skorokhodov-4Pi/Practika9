package com.example.praktika1

import adapter.OnInteractionListener
import adapter.PostsAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.praktika1.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val viewModel: PostViewModel by viewModels()
            val adapter = PostsAdapter(object : OnInteractionListener {
                override fun onVideo(post: Post) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=qeQnMgega0k"))
                    startActivity(intent)
                }
                override fun onEdit(post: Post) {
                    viewModel.edit(post)
                }
                override fun onLike(post: Post) {
                    viewModel.likeById(post.id)
                }
                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }
                override fun onShare(post: Post) {
                    viewModel.shareByid(post.id)
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, getString(R.string.sharepost))
                    startActivity(shareIntent)
                }
            })

            binding.list.adapter = adapter
            viewModel.data.observe(this) { posts ->
                adapter.submitList(posts)
            }
            val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
                result ?: return@registerForActivityResult
                viewModel.changeContent(result)
                viewModel.save()
            }
            viewModel.edited.observe(this){ post->
                if(post.id == 0L){
                    return@observe
                }
                newPostLauncher.launch(post.content)
            }
            binding.cancel.setOnClickListener {
                newPostLauncher.launch(null)
            }
            binding.fab.setOnClickListener {
                newPostLauncher.launch(null)
            }

        }


}

