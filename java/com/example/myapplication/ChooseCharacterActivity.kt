package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.MainMenu.MainMenuActivity
class ChooseCharacterActivity : AppCompatActivity() {

    private lateinit var maleButton: Button
    private lateinit var femaleButton: Button
    private lateinit var characterImageView: ImageView
    private lateinit var characterTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cc)
        maleButton = findViewById(R.id.maleButton)
        femaleButton = findViewById(R.id.femaleButton)
        characterImageView = findViewById(R.id.characterImageView)
        characterTextView = findViewById(R.id.ccTextView)
        maleButton.setOnClickListener {
            updateCharacter("male")
        }
        femaleButton.setOnClickListener {
            updateCharacter("female")
        }
    }
    private fun updateCharacter(gender: String) {
        val selectedArrayResourceId = when (gender) {
            "male" -> R.array.male
            "female" -> R.array.female
            else -> 0
        }
        val selectedArray = resources.getIntArray(selectedArrayResourceId)
        val intent = Intent(this, MainMenuActivity::class.java)
        intent.putExtra("selectedGender", gender)
        intent.putExtra("selectedArray", selectedArray)
        startActivity(intent)
    }
}