package com.example.myapplication.MainMenu

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.SharedViewModel
import com.example.myapplication.Shop.ShopItem
import com.example.myapplication.UniTime
class TimerActivity : AppCompatActivity(){
    companion object {
        const val TIMER_MINUTES = "timer_minutes"
        const val TIMER_INTERVAL_MILLIS = 1000L
    }
    private lateinit var textViewQuotes: TextView
    private val quotes = arrayOf(
        "‘Determine your priorities and focus on them’",
        "‘Starve your distraction, feed your focus'",
        "‘Just FOCUS on your priorities’",
        "‘Just do it!‘",
        "‘Ignore the noise, focus on your work!’"
    )
    private var currentQuoteIndex = 0
    private val handler = Handler()
    private lateinit var timerTextView: TextView
    private lateinit var stopButton: Button
    private lateinit var sharedViewModel: SharedViewModel
    private var timerMinutes: Int = 0
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var characterTimerImageView: ImageView
    private var isTimerRunning = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        characterTimerImageView = findViewById(R.id.characterTimerImageView)
        timerTextView = findViewById(R.id.timerTextView)
        stopButton = findViewById(R.id.stopButton)
        timerMinutes = intent.getIntExtra(TIMER_MINUTES, 0)
        sharedViewModel = ViewModelProvider(
            (application as UniTime).appViewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SharedViewModel::class.java)
        countDownTimer = object : CountDownTimer(timerMinutes * 60 * 1000L, TIMER_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / (60 * 1000)
                val seconds = (millisUntilFinished % (60 * 1000)) / 1000
                updateTimerText(minutes, seconds)
            }
            override fun onFinish() {
                updateTimerText(0, 0)
                navigateToBreakActivity()
                isTimerRunning=false
            }
        }.start()
        stopButton.setOnClickListener {
            showStopConfirmationPopup()
        }
        textViewQuotes = findViewById(R.id.textViewQuotes)
        updateTextView()
        val selectedGender = sharedViewModel.selectedGender.value
        val equip = sharedViewModel.equippedItem.value
        SelectCharacterImage(selectedGender, equip)
        handler.postDelayed(textViewUpdateRunnable, 60000)
    }
    private fun SelectCharacterImage(gender:String?,equippedItem:ShopItem?) {
        val selectedArray = when (gender) {
            "male" -> resources.obtainTypedArray(R.array.male)
            "female" -> resources.obtainTypedArray(R.array.female)
            else -> null
        }
        if (selectedArray != null) {
            val imageIndex = if (equippedItem != null) {
                equippedItem.index
            } else {
                0
            }
            Log.d("MainMenuActivity", "image index: $imageIndex")
            if (imageIndex < selectedArray.length()) {
                val imageId = selectedArray.getResourceId(imageIndex, 0)
                characterTimerImageView.setImageResource(imageId)
            } else {
                Log.e("MainMenuActivity", "Invalid array index")
            }
            selectedArray.recycle()
        } else {
            Log.e("MainMenuActivity", "Invalid selected gender")
        }
    }
    private val textViewUpdateRunnable = object : Runnable {
        override fun run() {
            currentQuoteIndex = (currentQuoteIndex + 1) % quotes.size
            updateTextView()
            handler.postDelayed(this, 60000)
        }
    }
    private fun updateTextView() {
        textViewQuotes.text = quotes[currentQuoteIndex]
    }
    private fun updateTimerText(minutes: Long, seconds: Long) {
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }
    private fun showStopConfirmationPopup() {
        val dialogBuilder = AlertDialog.Builder(this,R.style.DialogTheme)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_timer_layout, null)
        val btnStop = dialogView.findViewById<Button>(R.id.btnStop)
        val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        btnStop.setOnClickListener {
            stopTimerAndNavigateToSetTimeFragment()
            alertDialog.dismiss()
        }
        btnClose.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    private fun stopTimerAndNavigateToSetTimeFragment() {
        countDownTimer.cancel()
        timerMinutes = 0
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToBreakActivity() {
        val equip = sharedViewModel.equippedItem.value
        val equippedReward = calculateReward(equip?.power ?: 0)
        val defaultReward = calculateReward(0)
        val reward = if (equip != null) {
            equippedReward
        } else {
            defaultReward
        }
        sharedViewModel.increaseCurrency(reward)
        sharedViewModel.updateAchievementProgress(  1,1)
        sharedViewModel.updateTimerAchievement(2,timerMinutes)
        sharedViewModel.updateTimerAchievement(3,timerMinutes)
        sharedViewModel.updateTimerAchievement(4,timerMinutes)
        sharedViewModel.updateTimerAchievement(5,timerMinutes)
        sharedViewModel.updateTimerAchievement(6,timerMinutes)
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, BreakActivity::class.java)
            intent.putExtra(BreakActivity.EARNED_REWARD, reward)
            intent.putExtra("selectedGender",sharedViewModel.selectedGender.value)
            startActivity(intent)
            finish()
        }, 500)
    }
    private fun calculateReward(power: Int): Int {
        val baseReward = timerMinutes / 5 * 10
        return baseReward + power
    }
     override fun onDestroy() {
        super.onDestroy()
        timerMinutes= 0
        updateTimerText(0,0)
        isTimerRunning=false
    }
    override fun onResume() {
        super.onResume()
        if (isTimerRunning) {
            hideOverlay()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isTimerRunning) {
            showOverlay()
        }
    }
    private fun showOverlay() {
        val overlayIntent = Intent(this, ForegroundAppService::class.java)
        ContextCompat.startForegroundService(this, overlayIntent)
        Log.d("TimerActivity", "here")
    }

    private fun hideOverlay() {
        val overlayIntent = Intent(this, ForegroundAppService::class.java)
        stopService(overlayIntent)
        Log.d("TimerActivity", "hereHIIDEEE")
    }
}
