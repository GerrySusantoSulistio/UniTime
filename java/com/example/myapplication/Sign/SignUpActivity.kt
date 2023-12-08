package com.example.myapplication.Sign

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
class SignUpActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val etSignupName = findViewById<EditText>(R.id.etNewName)
        val etSignupEmail = findViewById<EditText>(R.id.etNewUsername)
        val etSignupPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val signIn = findViewById<TextView>(R.id.LoginBtn)
        val auth = FirebaseAuth.getInstance()
        signIn.setOnClickListener{ startActivity(Intent(this, LoginActivity::class.java))}
        btnSignup.setOnClickListener {
            val name = etSignupName.text.toString()
            val email = etSignupEmail.text.toString()
            val password = etSignupPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(baseContext, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(baseContext, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                         val user = auth.currentUser
                         val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profileUpdates)
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        Toast.makeText(baseContext, "Sign up failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}