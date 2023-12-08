package com.example.myapplication.Shop

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.Achievement.AchievementActivity
import com.example.myapplication.MainMenu.MainMenuActivity
import com.example.myapplication.PopupAdapter
import com.example.myapplication.SettingsActivity
import com.example.myapplication.SharedViewModel
import com.example.myapplication.UniTime
import com.google.android.material.navigation.NavigationView

class ShopActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var singlePullButton: ImageButton
    private lateinit var multiPullButton: ImageButton
    private lateinit var currencyTextView: TextView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var shopRecyclerView: RecyclerView
    private var equippedItem: ShopItem? = null
    private lateinit var shopAdapter: ShopAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val gachaAdapterObserver = Observer<List<ShopItem>> { items ->
        items?.let {
            shopAdapter.updateData(it)
        }
    }
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)
        currencyTextView = findViewById(R.id.currencyTextView)
        singlePullButton = findViewById(R.id.singlePullButton)
        multiPullButton = findViewById(R.id.multiPullButton)
        shopRecyclerView = findViewById(R.id.gachaRecyclerView)
        singlePullButton.setOnClickListener { onPullButtonClick(1) }
        multiPullButton.setOnClickListener { onPullButtonClick(5) }
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        shopAdapter = ShopAdapter(
            emptyList(),
            { clickedItem -> showItemDetailsPopup(clickedItem) },
            { equippedItem -> handleEquippedItem(equippedItem) }
        )
        shopRecyclerView.layoutManager = GridLayoutManager(this, 3)
        shopRecyclerView.adapter = shopAdapter
        sharedViewModel = ViewModelProvider(
            (application as UniTime).appViewModelStore,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(SharedViewModel::class.java)
        sharedViewModel.userCurrency.observe(this) { userCurrency ->
            currencyTextView.text = "${userCurrency.amount}"
        }
        sharedViewModel.gachaItems.observe(this, gachaAdapterObserver)
        sharedViewModel.equippedItem.observe(this) { item ->
            equippedItem = item
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
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_achievement -> {
                startActivity(Intent(this, AchievementActivity::class.java))
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
    private fun onPullButtonClick(count: Int) {
        var cost : Int
        if(count==1){
            cost = 250
        }else{
            cost = 250 * (count-1)
        }
        if (hasEnoughCurrency(cost)) {
            val pulledItems = sharedViewModel.pullGacha(count)
            sharedViewModel.decreaseCurrency(cost)
            showResultPopup(pulledItems, count)
        } else {
            showInsufficientCurrencyAlert()
        }
        updateDataAchive()
    }
    private fun hasEnoughCurrency(cost: Int): Boolean {
        return (sharedViewModel.userCurrency.value?.amount ?: 0) >= cost
    }
    private fun showInsufficientCurrencyAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Insufficient Currency")
            .setMessage("You don't have enough currency to perform this action.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog = builder.create()
        alertDialog.show()
    }
    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showItemDetailsPopup(clickedItem: ShopItem) {
        val popupView = layoutInflater.inflate(R.layout.popup_gacha_detail, null)
        val itemNameTextView: TextView = popupView.findViewById(R.id.itemNameTextView)
        val itemDescriptionTextView: TextView = popupView.findViewById(R.id.itemDescriptionTextView)
        val itemIconView: ImageView = popupView.findViewById(R.id.itemIconDetailView)
        val itemsArray = popupView.resources.obtainTypedArray(R.array.items_icon)
        val itemDrawableResId = itemsArray.getResourceId(clickedItem.index - 1, 0)
        itemsArray.recycle()
        itemIconView.setImageResource(itemDrawableResId)
        val textColorResId = when (clickedItem.rarity) {
            Rarity.COMMON -> R.color.common
            Rarity.RARE -> R.color.rare
            Rarity.EPIC -> R.color.epic
        }
        itemNameTextView.setTextColor(ContextCompat.getColor(popupView.context, textColorResId))
        itemNameTextView.text = "${clickedItem.name}"
        itemDescriptionTextView.text = "Add ${clickedItem.power} time coins every 5 minutes"
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(findViewById<View>(android.R.id.content), Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            popupWindow.dismiss()
        }
    }
    private fun handleEquippedItem(item: ShopItem) {
        if (item.isCountingUp) {
            return
        }
        item.isEquipped = !item.isEquipped
        if (item.isEquipped) {
            equippedItem = item
        } else {
            equippedItem = null
        }
        sharedViewModel.setEquippedItem(equippedItem)
        handler.post {
            updateEquippedStatusInUI()
            shopAdapter.setEquippedItem(equippedItem)
        }
    }

    private fun updateEquippedStatusInUI() {
        for (gachaItem in shopAdapter.getItems()) {
            gachaItem.isEquipButtonEnabled = (equippedItem == null || gachaItem == equippedItem)
            gachaItem.isEquipped = gachaItem == equippedItem

        }
        shopAdapter.notifyDataSetChanged()
    }
    fun updateDataAchive() {
        val visibleItemCount = shopAdapter.getVisibleItemCount()
        Log.d("ShopAdapter", "Visible Item Count: $visibleItemCount")
        sharedViewModel.updateAchievementProgress(7, visibleItemCount)
        sharedViewModel.updateAchievementProgress(8, visibleItemCount)
        sharedViewModel.updateAchievementProgress(9, visibleItemCount)
    }
    @SuppressLint("InflateParams")
    private fun showResultPopup(pulledItem: List<ShopItem>, count: Int) {
        val layoutResId = if (count == 1) R.layout.popup_result_single else R.layout.popup_result_multi
        val popupView = layoutInflater.inflate(layoutResId, null)
        val resultRecyclerView: RecyclerView = popupView.findViewById(R.id.resultRecyclerView)

        val popupAdapter = PopupAdapter(pulledItem,count==1)
        resultRecyclerView.adapter = popupAdapter

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0)
        popupWindow.setOnDismissListener {
            popupWindow.dismiss()
        }
    }
}