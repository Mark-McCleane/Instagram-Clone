package com.example.clonestagram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clonestagram.Models.Post
import com.example.clonestagram.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

private const val TAG = "PostsActivity"
private const val EXTRAUSERNAME = "EXTRAUSERNAME"

open class PostsActivity : AppCompatActivity() {

    private lateinit var fireStoreDB: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter
    private lateinit var recyclerViewPosts: RecyclerView
    private var signedInUser:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        posts = mutableListOf()
        adapter = PostsAdapter(this, posts)
        recyclerViewPosts = findViewById(R.id.recyclerview_posts)
        recyclerViewPosts.adapter = adapter
        recyclerViewPosts.layoutManager = LinearLayoutManager(this)

        // TODO Move to Repository folder when doing MVVM
        val fireStoreDB = FirebaseFirestore.getInstance()
        fireStoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get().addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener{exception ->
                Log.i(TAG, "Failure fetching singed in user", exception)
            }

        var postsReference = fireStoreDB
            .collection("posts")
            .limit(20)
            .orderBy("createdAt", Query.Direction.DESCENDING)

        val username = intent.getStringExtra(EXTRAUSERNAME)
        if (username != null) {
            supportActionBar?.title = username
            postsReference = postsReference.whereEqualTo("user.username", username)
        }

        postsReference.addSnapshotListener { snapshot, exception ->
            if (exception != null || snapshot == null) {
                Log.e(TAG, "Exception when querying Firestore Posts")
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            posts.clear()
            posts.addAll(postList)
            adapter.notifyDataSetChanged()
            for (post in postList) {
                Log.i(TAG, "Post: $post")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_profile) {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(EXTRAUSERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}