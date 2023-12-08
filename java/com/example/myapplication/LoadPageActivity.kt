package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ProgressBar
import com.example.myapplication.Sign.LoginActivity
class LoadPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loadingpage)
        val progressBar= findViewById<ProgressBar>(R.id.customProgressBar)
        val handler = Handler()
        val progressRunnable = object : Runnable {
            override fun run() {
                if (progressBar.progress < progressBar.max) {
                    progressBar.progress += 5 // Adjust the increment as needed
                    handler.postDelayed(this, 100) // Adjust the delay as needed
                } else if(progressBar.progress == progressBar.max) {
                    val intent = Intent(this@LoadPageActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    progressBar.progress = 0
                    handler.postDelayed(this, 100)
                }
            }
        }
        handler.post(progressRunnable)
    }
}