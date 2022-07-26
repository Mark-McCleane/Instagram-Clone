package com.example.clonestagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Exception

private const val TAG = "PostsActivity"
class PostsActivity : AppCompatActivity() {
    private lateinit var fireStoreDB: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        // Firestore query
        val fireStoreDB = FirebaseFirestore.getInstance()
        val postsReference = fireStoreDB.collection("posts")
        postsReference.addSnapshotListener{snapshot, exception ->
            if(exception != null || snapshot == null ){
                Log.e(TAG, "Exception when querying Firestore Posts")
                return@addSnapshotListener
            }

            for(document in snapshot.documents){
                Log.i(TAG, "Document ${document.id}: ${document.data}")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile){
            val intent: Intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}