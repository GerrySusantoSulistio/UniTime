package com.example.myapplication.introduction


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.ChooseCharacterActivity
import com.example.myapplication.R
class IntroActivity : AppCompatActivity(){
    private lateinit var viewPager: ViewPager2
    private lateinit var dotContainer: LinearLayout
    private lateinit var dotViews: Array<ImageView>
    private lateinit var introAdapter: IntroAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introduction)
        viewPager = findViewById(R.id.viewPager)
        dotContainer = findViewById(R.id.dotContainer)
        val items = listOf(
            R.drawable.intro1,
            R.drawable.intro2,
            R.drawable.intro3,
            R.drawable.intro4
        )
        introAdapter = IntroAdapter(items)
        viewPager.adapter = introAdapter
        viewPager.setPadding(0,100,0,10)
        dotViews = Array(items.size) { index ->
            val dot = LayoutInflater.from(this).inflate(R.layout.dot_indicator, dotContainer, false) as ImageView
            dotContainer.addView(dot)
            dot
        }
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDotIndicator(position)
                if (position == introAdapter.itemCount - 1) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (viewPager.currentItem == introAdapter.itemCount - 1) {
                            navigateToChooseCharacterActivity()
                        }
                    }, 1000)
                }
            }
        })
    }
    private fun updateDotIndicator(currentPosition: Int) {
        dotViews.forEachIndexed { index, dotView ->
            val drawableResId = if (index == currentPosition) {
                R.drawable.indi_intro_active
            } else {
                R.drawable.indi_intro_inactive
            }
            dotView.setImageResource(drawableResId)
        }
    }
    private fun navigateToChooseCharacterActivity() {
        val intent = Intent(this, ChooseCharacterActivity::class.java)
        startActivity(intent)
        finish()
    }
}