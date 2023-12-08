package com.example.myapplication.Sign

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainMenu.MainMenuActivity
import com.example.myapplication.R
import com.example.myapplication.introduction.IntroActivity
import com.google.firebase.auth.FirebaseAuth
class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val signUp = findViewById<TextView>(R.id.SignUpBtn)
        auth = FirebaseAuth.getInstance()
        signUp.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val currentUser = auth.currentUser
                        if (currentUser != null) {
                            if (task.result?.additionalUserInfo?.isNewUser == true) {
                                startActivity(Intent(this, MainMenuActivity::class.java))
                            } else {
                                startActivity(Intent(this, IntroActivity::class.java))
                            }
                        }
                    } else {
                        Toast.makeText(baseContext, "Login failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}


