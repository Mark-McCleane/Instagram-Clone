package com.example.clonestagram

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    private lateinit var btnLogin: Button
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var auth: FirebaseAuth;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        btnLogin = findViewById(R.id.btnLogin)
        usernameEditText = findViewById(R.id.EmailAddressLoginField)
        passwordEditText = findViewById(R.id.PasswordLoginField)

        val mAuth = Firebase.auth
        if(mAuth.currentUser != null){
            goToFeedActivity()
        }

        btnLogin.setOnClickListener {
            btnLogin.isEnabled = false
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email OR Password is Blank", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            //TODO Firebase Auth
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Succesful", Toast.LENGTH_LONG).show()
                    goToFeedActivity()
                } else {
                    Log.e(TAG, "Login Failed", task.exception)
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun goToFeedActivity() {
        Log.i(TAG, "goToFeedActivity")
        val intent: Intent = Intent(this, PostsActivity::class.java)
        startActivity(intent)
        finish()
    }
}