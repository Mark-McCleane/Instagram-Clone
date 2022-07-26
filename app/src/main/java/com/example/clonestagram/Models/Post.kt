package com.example.clonestagram.Models

import com.google.firebase.firestore.PropertyName

data class Post(
    var description: String = "",

    @get:PropertyName("image_url") @set:PropertyName("image_url")
    var imageUrl: String = "",

    @get:PropertyName("createdAt") @set:PropertyName("createdAt")
    var creationTimeInMilliseconds: Long = 0L,
    var user: User? = null,
)
