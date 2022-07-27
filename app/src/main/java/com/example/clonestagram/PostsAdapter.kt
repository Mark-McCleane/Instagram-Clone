package com.example.clonestagram

import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.clonestagram.Models.Post

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
        val textViewRelativeTime = itemView.findViewById<TextView>(R.id.textView_RelativeTime)

        fun bind(post:Post){
            textViewUserName.text = post.user?.username
            textViewDescription.text = post.description
            Glide.with(context).load(post.imageUrl).into(imageViewPostImage)
            textViewRelativeTime.text = DateUtils.getRelativeTimeSpanString(post.creationTimeInMilliseconds)
        }
    }
}