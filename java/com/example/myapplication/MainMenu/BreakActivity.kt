package com.example.myapplication.MainMenu

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.UniTime
import com.example.myapplication.SharedViewModel
class BreakActivity : AppCompatActivity() {
    companion object {
        const val EARNED_REWARD="EARNED_REWARD"
        const val BREAK_TIMER_INTERVAL_MILLIS = 1000L
    }
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var breakSeekBar: SeekBar
    private lateinit var breakTimerTextView: TextView
    private lateinit var startBreakButton: Button
    private lateinit var backButton: ImageView
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var currencyTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_break)
        breakSeekBar = findViewById(R.id.breakSeekBar)
        breakTimerTextView = findViewById(R.id.breakTimerTextView)
        startBreakButton = findViewById(R.id.startBreakButton)
        currencyTextView = findViewById(R.id.userCurrencyTextView)
        breakSeekBar.max = 60
        sharedViewModel = ViewModelProvider(
            (application as UniTime).appViewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SharedViewModel::class.java)
        startBreakButton.setOnClickListener {
            startBreakTimer()
        }
        val selectedGender = sharedViewModel.selectedGender.value
        Log.d("BreakActivity", "Gender After $selectedGender")
        if (selectedGender != null) {
            sharedViewModel.setSelectedGender(selectedGender)
        }
        breakSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateBreakTimerText(progress * 60 * 1000L)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
        showBreakPopup()
        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            navigateToSetTimerFragment()
        }
        sharedViewModel.userCurrency.observe(this) { userCurrency ->
            currencyTextView.text = "${userCurrency.amount}"
        }
    }
    private fun navigateToSetTimerFragment() {
        val intent = Intent(this, MainMenuActivity::class.java)
        startActivity(intent)
    }
    private fun showBreakPopup() {
        val dialog = Dialog(this,R.style.DialogTheme)
        val view: View = LayoutInflater.from(this).inflate(R.layout.popup_break, null)
        dialog.setContentView(view)
        val earnedReward = intent.getIntExtra(EARNED_REWARD, 0)
        val popupRewardTextView : TextView = view.findViewById(R.id.popupRewardText)
        popupRewardTextView.text = "+$earnedReward"
        dialog.show()
    }
    private fun updateBreakTimerText(millisUntilFinished: Long) {
        val minutes = millisUntilFinished / (60 * 1000)
        val seconds = (millisUntilFinished % (60 * 1000)) / 1000
        breakTimerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }
    private fun startBreakTimer() {
        val breakTimeMillis = breakSeekBar.progress * 60 * 1000L
        breakSeekBar.visibility = View.GONE
        startBreakButton.visibility = View.GONE
        countDownTimer = object : CountDownTimer(breakTimeMillis, BREAK_TIMER_INTERVAL_MILLIS) {
            override fun onTick(millisUntilFinished: Long) {
                updateBreakTimerText(millisUntilFinished)
            }
            override fun onFinish() {
                updateBreakTimerText(0)
            }
        }.start()
    }
}