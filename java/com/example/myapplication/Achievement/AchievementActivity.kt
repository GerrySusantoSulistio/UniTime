package com.example.myapplication.Achievement

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.MainMenu.MainMenuActivity
import com.example.myapplication.R
import com.example.myapplication.SettingsActivity
import com.example.myapplication.SharedViewModel
import com.example.myapplication.Shop.ShopActivity
import com.example.myapplication.UniTime

import com.google.android.material.navigation.NavigationView

class AchievementActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var currencyTextView: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var achievementRecyclerView: RecyclerView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var achievementAdapter: AchievementAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievement)
        currencyTextView = findViewById(R.id.currencyTextView)
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        achievementRecyclerView = findViewById(R.id.achievementRecyclerView)
        sharedViewModel = ViewModelProvider(
            (application as UniTime).appViewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SharedViewModel::class.java)
        achievementAdapter = AchievementAdapter(emptyList(),sharedViewModel)
        achievementRecyclerView.layoutManager = LinearLayoutManager(this)
        achievementRecyclerView.adapter = achievementAdapter
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu_icon)
        navigationView.setNavigationItemSelectedListener(this)
        sharedViewModel.achievements.observe(this, Observer { achievements ->
            achievements?.let {
            achievementAdapter.updateData(it)}
        })
        sharedViewModel.userCurrency.observe(this) { userCurrency ->
            currencyTextView.text = "${userCurrency.amount}"
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_shop -> {
                startActivity(Intent(this, ShopActivity::class.java))
                return true
            }
            R.id.menu_main -> {
                startActivity(Intent(this, MainMenuActivity::class.java))
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
}