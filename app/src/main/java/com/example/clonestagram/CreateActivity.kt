package com.example.clonestagram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clonestagram.Models.Post
import com.example.clonestagram.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class CreateActivity : AppCompatActivity() {
    private lateinit var etDescription: EditText
    private lateinit var btnPickImage: Button
    private lateinit var userSelectedImage: ImageView
    private lateinit var btnSubmit: Button
    private lateinit var fireStoreDB: FirebaseFirestore
    private lateinit var firebaseStorageRef: StorageReference
    private var TAG = "onCreate"
    private var PICKER_PHOTO_CODE = 1234
    private var photoUri: Uri? = null
    private var signedInUser: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)
        firebaseStorageRef = FirebaseStorage.getInstance().reference
        btnPickImage = findViewById(R.id.btnPickImage)
        userSelectedImage = findViewById(R.id.iv_userselectedimage)
        btnSubmit = findViewById(R.id.btnSubmit)
        etDescription = findViewById<EditText>(R.id.etDescription)

        fireStoreDB = FirebaseFirestore.getInstance()
        fireStoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get().addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(User::class.java)
                Log.i(TAG, "signed in user: $signedInUser")
            }
            .addOnFailureListener{exception ->
                Log.i(TAG, "Failure fetching singed in user", exception)
            }

        btnPickImage.setOnClickListener{
            Log.i(TAG, "Opened Image Picker")
            val imagePickerIntent = Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type = "image/*"
            startActivityForResult(imagePickerIntent, PICKER_PHOTO_CODE)
        }

        btnSubmit.setOnClickListener {
            handleSubmitButton()
        }
    }

    private fun handleSubmitButton() {
        if(photoUri == null){
            Toast.makeText(this, "No Photo Selected", Toast.LENGTH_SHORT).show()
            return
        }
        if(etDescription.text.isBlank()){
            Toast.makeText(this, "No Description Inputted", Toast.LENGTH_SHORT).show()
            return
        }

        if(signedInUser == null){
            Toast.makeText(this, "No Signed In User, Please Sign In", Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        val photoUploadUri = photoUri as Uri
        val photoReference = firebaseStorageRef.child("images/${System.currentTimeMillis()} - photo.jp")
        //        1. upload photo firebase storage
        photoReference.putFile(photoUploadUri).continueWith{photoUploadTask ->
            //        2. Retrieve image url
            Log.i(TAG, "Upload Bytes : ${photoUploadTask.result?.bytesTransferred}")
            photoReference.downloadUrl
        }.continueWithTask { downloadURlTask ->
            //        3. Create post object with image url and add to post collections
            val post = Post(
                etDescription.text.toString(),
                downloadURlTask.result.toString(),
                System.currentTimeMillis(),
                signedInUser)
            fireStoreDB.collection("posts").add(post)
        }.addOnCompleteListener { postCreationTask ->
            btnSubmit.isEnabled = true
            if(!postCreationTask.isSuccessful){
                Log.e(TAG, "Exception during Firebase operations", postCreationTask.exception)
                Toast.makeText(this, "Failed to send post", Toast.LENGTH_SHORT).show()
            }
            etDescription.text.clear()
            userSelectedImage.setImageResource(0)
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
            val profileIntent = Intent(this, ProfileActivity::class.java)
            profileIntent.putExtra(EXTRAUSERNAME, signedInUser?.username)
            startActivity(profileIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICKER_PHOTO_CODE){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                Log.i(TAG, "photoUri: $photoUri")
                userSelectedImage.setImageURI(photoUri)
            }
            else{
                Toast.makeText(this, "Image Picker Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}