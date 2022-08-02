package com.example.clonestagram

import android.content.Context
import android.media.Image
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clonestagram.Models.Post
import java.math.BigInteger
import java.security.MessageDigest

class PostsAdapter(val context: Context, val posts: List<Post>):
    RecyclerView.Adapter<PostsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val textViewUserName = itemView.findViewById<TextView>(R.id.textView_Username)
        val textViewDescription = itemView.findViewById<TextView>(R.id.textView_Description)
        val imageViewPostImage = itemView.findViewById<ImageView>(R.id.imageView_PostImage)
        val imageViewProfileImage = itemView.findViewById<ImageView>(R.id.imageView_PostImage)
        val textViewRelativeTime = itemView.findViewById<TextView>(R.id.textView_RelativeTime)


        fun bind(post:Post){
            val username = post.user?.username as String
            textViewUserName.text = username
            textViewDescription.text = post.description
            Glide.with(context).load(post.imageUrl).into(imageViewPostImage)
            Glide.with(context).load(getProfileImageUrl(username)).into(imageViewProfileImage)
            textViewRelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeInMilliseconds)
        }

        private fun getProfileImageUrl(username: String): String {
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(username.toByteArray())
            val bigInt = BigInteger(hash)
            val hex = bigInt.abs().toString(16)
            return "https://www.gravatar.com/avatar/$hex?d=identicon"
        }
    }
}