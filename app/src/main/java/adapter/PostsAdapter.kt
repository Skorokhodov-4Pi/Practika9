package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.praktika1.Post
import com.example.praktika1.R
import com.example.praktika1.databinding.ActivityCardPostBinding
import kotlin.math.ln
import kotlin.math.pow

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onShare(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onVideo(post: Post) {}
}


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ActivityCardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}
class PostViewHolder(
    private val binding: ActivityCardPostBinding,
    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            mainText.text = post.content
            textView5.text = post.likes.toString()
            textView6.text = post.share.toString()
            imageButton7.isChecked = post.likedByMe
            textView5.text = getFormatedNumber(post.likes.toLong())
            textView6.text = getFormatedNumber(post.share.toLong())
            imageButton4.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            imageButton7.setOnClickListener{
                onInteractionListener.onLike(post)
            }
            textView5.text = post.likes.toString()

            imageButton8.setOnClickListener{
                onInteractionListener.onShare(post)
            }
            video.setOnClickListener {
                onInteractionListener.onVideo(post)
            }
            play.setOnClickListener {
                onInteractionListener.onVideo(post)
            }

            when {
                post.likes in 1000..999999 -> textView5.text = "${post.likes / 1000}K"
                post.likes < 1000 -> textView5.text = post.likes.toString()
                else -> textView5.text = String.format("%.1fM", post.likes.toDouble() / 1000000)
            }
            textView6.text = post.share.toString()
            when {
                post.share < 1000 -> textView6.text = post.share.toString()
                post.share in 1000..999999 -> textView6.text = "${post.share / 1000}K"
                else -> textView6.text = String.format(
                    "%.1fM", post.share.toDouble() / 1000000
                )
            }

        }
    }

}

fun getFormatedNumber(count: Long): String {
    if (count < 1000) return "" + count
    val exp = (ln(count.toDouble()) / ln(1000.0)).toInt()
    return String.format("%.1f %c", count / 1000.0.pow(exp.toDouble()), "KMGTPE"[exp - 1])
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}