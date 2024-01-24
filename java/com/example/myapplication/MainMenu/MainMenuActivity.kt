package com.example.myapplication.MainMenu

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.Achievement.AchievementActivity
import com.example.myapplication.SettingsActivity
import me.tankery.lib.circularseekbar.CircularSeekBar;
import com.example.myapplication.SharedViewModel
import com.example.myapplication.Shop.ShopActivity
import com.example.myapplication.Shop.ShopItem
import com.example.myapplication.UniTime
import com.google.android.material.navigation.NavigationView

class MainMenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var seekBar: CircularSeekBar
    private lateinit var textView: TextView
    private lateinit var setTimerButton: Button
    private lateinit var userCurrencyTextView: TextView
    private lateinit var rewardTextView: TextView
    private lateinit var characterImageView: ImageView
    private var selectedTimerMinutes: Int = 0
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var selectedGender:String
    private lateinit var permissionDialog: AlertDialog
    private val PERMISSION_REQUEST_FOREGROUND_SERVICE = 123

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        setTimerButton = findViewById(R.id.setTimerButton)
        seekBar = findViewById(R.id.seekBar)
        textView = findViewById(R.id.textView)
        userCurrencyTextView = findViewById(R.id.currencyTextView)
        rewardTextView = findViewById(R.id.rewardTextView)
        characterImageView = findViewById(R.id.characterImageView)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)
        sharedViewModel = ViewModelProvider(
            (application as UniTime).appViewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SharedViewModel::class.java)
        seekBar.setOnSeekBarChangeListener(object : CircularSeekBar.OnCircularSeekBarChangeListener {
            override fun onProgressChanged(circularSeekBar: CircularSeekBar?, progress: Float, fromUser: Boolean) {
                selectedTimerMinutes = (progress * 5).toInt()
                selectedTimerMinutes = selectedTimerMinutes.coerceIn(0, 120)
                updateTimerText(selectedTimerMinutes)
                val equip = sharedViewModel.equippedItem.value
                updateRewardText(calculateReward(progress.toInt()), equip?.power ?: 0)
            }
            override fun onStartTrackingTouch(seekBar: CircularSeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: CircularSeekBar?) {
            }
        })
        updateTimerText(selectedTimerMinutes)
        val genderSelectionCount =  sharedViewModel.genderCount.value
        selectedGender = intent.getStringExtra("selectedGender").toString()
        if (genderSelectionCount == 0){
            sharedViewModel.setSelectedGender(selectedGender)
            SelectCharacterImage(selectedGender,null)
            sharedViewModel.increaseGenderCount()
        }
        val selectGender = sharedViewModel.selectedGender.value
        val equip = sharedViewModel.equippedItem.value
        SelectCharacterImage(selectGender, equip)
        updateRewardText(calculateReward(seekBar.progress.toInt()),equip?.power ?: 0)
        setTimerButton.setOnClickListener {
            if (checkPermissions()) {
                navigateToTimerActivity(selectedTimerMinutes)
            } else {
                showPermissionDialog()
            }
        }
        sharedViewModel.userCurrency.observe(this) { userCurrency ->
            userCurrencyTextView.text = "${userCurrency.amount}"
        }
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.title=""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu_icon)
        navigationView.setNavigationItemSelectedListener(this)
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
            if (imageIndex < selectedArray.length()) {
                val imageId = selectedArray.getResourceId(imageIndex, 0)
                characterImageView.setImageResource(imageId)
            } else {
                Log.e("MainMenuActivity", "Invalid array index")
            }
            selectedArray.recycle()
        } else {
            Log.e("MainMenuActivity", "Invalid selected gender")
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_shop -> {
                startActivity(Intent(this, ShopActivity::class.java))
                return true
            }
            R.id.menu_achievement -> {
                startActivity(Intent(this, AchievementActivity::class.java))
                return true
            }
            R.id.menu_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    private fun updateTimerText(minutes: Int) {
        val roundedMinutes = (minutes/5) * 5
        val displayMinutes = if (roundedMinutes + 10 > 120) 120 else roundedMinutes + 10
        val formattedMinutes = String.format("%02d", displayMinutes)
        textView.text = "$formattedMinutes:00"
    }
    private fun navigateToTimerActivity(timerMinutes: Int) {
        val roundedMinutes = (timerMinutes/5) * 5
        val equip = sharedViewModel.equippedItem.value
        sharedViewModel.setEquippedItem(equip)
        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra(TimerActivity.TIMER_MINUTES, if (roundedMinutes+10> 120) 120 else roundedMinutes+ 10)
        startActivity(intent)
    }
    private fun updateRewardText(reward: Int,power: Int) {
        val displayRewards = if (reward + 20 > 240) 240 +power else reward + 20 + power
        rewardTextView.text = "+$displayRewards"
    }
    private fun calculateReward(progress: Int): Int {
        val baseReward = progress * 10
        return baseReward
    }
    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
        val view = LayoutInflater.from(this).inflate(R.layout.permission_dialog, null)

        val systemAlertWindowSwitch = view.findViewById<Switch>(R.id.systemAlertWindowSwitch)

        systemAlertWindowSwitch.isChecked = checkSystemAlertWindowPermission()

        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, _ ->
            // Handle permission switches here

            if (systemAlertWindowSwitch.isChecked) {
                requestSystemAlertWindowPermission()
            }
                requestForegroundServicePermission()
            if (checkPermissions()) {
                navigateToTimerActivity(selectedTimerMinutes)
            } else {
                Toast.makeText(this, "Required permissions not granted", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        permissionDialog = builder.create()
        permissionDialog.show()
    }


    private fun checkSystemAlertWindowPermission(): Boolean {

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }
    private fun checkForegroundPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.FOREGROUND_SERVICE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForegroundServicePermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.FOREGROUND_SERVICE),
            PERMISSION_REQUEST_FOREGROUND_SERVICE
        )
    }

    private fun requestSystemAlertWindowPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }
}
