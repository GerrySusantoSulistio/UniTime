package com.example.myapplication

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.myapplication.Achievement.Achievement
import com.example.myapplication.Shop.Rarity
import com.example.myapplication.Shop.ShopItem

import kotlin.random.Random

data class UserCurrency(
    var amount: Int
)
class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val _userCurrency = MutableLiveData<UserCurrency>()
    val userCurrency: LiveData<UserCurrency> get() = _userCurrency

    private val _equippedItem = MutableLiveData<ShopItem?>()
    val equippedItem: LiveData<ShopItem?> get() = _equippedItem

    private val _gachaItems = MutableLiveData<List<ShopItem>>()
    val gachaItems: LiveData<List<ShopItem>> get() = _gachaItems

    private val _achievements = MutableLiveData<List<Achievement>?>()
    val achievements: MutableLiveData<List<Achievement>?> get() = _achievements

    private val _selectedGender = MutableLiveData<String>()
    val selectedGender: LiveData<String> get() = _selectedGender

    private val sharedPreferences = application.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

    private val _completedAchievement = MutableLiveData<Achievement?>()
    private val _totalTimerUsage = MutableLiveData<Int>()
    val totalTimerUsage: LiveData<Int> get() = _totalTimerUsage
    val completedAchievement: LiveData<Achievement?> get() = _completedAchievement
    private var isFirstEntry = true
    private val _genderCount = MutableLiveData<Int>()
    val genderCount: LiveData<Int> get() = _genderCount
    init {
        initializeUserCurrency()
        initializeGachaItems()
        initializeAchievement()
        retrieveEquippedItem()
        _equippedItem.value = null
        _totalTimerUsage.value = 0
        _genderCount.value = 0
    }
    fun initializeUserCurrency() {
        if (isFirstEntry) {
            _userCurrency.value = UserCurrency(0)
            isFirstEntry = false
        }
    }
    fun increaseGenderCount() {
        val currentCount = _genderCount.value ?: 0
        _genderCount.value = currentCount + 1
    }
    fun increaseCurrency(amount: Int) {
        val currentCurrency = _userCurrency.value
        currentCurrency?.let {
            _userCurrency.value = UserCurrency(it.amount + amount)
        }
    }
    fun decreaseCurrency(amount: Int) {
        val currentCurrency = _userCurrency.value
        currentCurrency?.let {
            _userCurrency.value = UserCurrency(it.amount - amount)
        }
    }
    fun setEquippedItem(item: ShopItem?) {
        _equippedItem.value = item
        saveEquippedItem(item)
    }
    private fun saveEquippedItem(item: ShopItem?) {
        val editor = sharedPreferences.edit()
        editor.putInt("equippedItemId", item?.index ?: -1)
        editor.apply()
    }
    private fun retrieveEquippedItem() {
        val equippedItemId = sharedPreferences.getInt("equippedItemId", -1)
        val item = _gachaItems.value?.find { it.index == equippedItemId }
        _equippedItem.value = item
    }
    fun setSelectedGender(gender: String) {
        _selectedGender.value = gender
    }
    private fun initializeGachaItems() {
        // Initialize with some default Gacha items
        _gachaItems.value = listOf(
            ShopItem(1,"HANDPHONE", Rarity.COMMON, 7, 5),
            ShopItem(2,"LAPTOP", Rarity.COMMON, 7, 5),
            ShopItem(3,"MINERAL WATER", Rarity.COMMON, 7, 5),
            ShopItem(4,"NOTES", Rarity.RARE, 2, 10),
            ShopItem(5,"COFFEE", Rarity.RARE, 2, 10),
            ShopItem(6,"BOOK", Rarity.RARE, 2, 10),
            ShopItem(7,"INSTANT NOODLE", Rarity.EPIC, 1, 30),
            ShopItem(8,"SODA", Rarity.EPIC, 1, 30),
            ShopItem(9,"COMPUTER", Rarity.EPIC, 1, 30),
        )
    }
    private fun initializeAchievement(){
        _achievements.value = listOf(
            Achievement(1, "Pomodoro Timer", "First time using pomodoro timer", "timer",  0, 1, 250),
            Achievement(2, "Beginner Student", "Total focused time reached 5 hours", "timer", 0, 5, 100),
            Achievement(3, "Amatur Student", "Total focused time reached 3 days", "timer",  0, 3, 150),
            Achievement(4, "Expert Student", "Total focused time reached 7 days", "timer",  0, 7, 200),
            Achievement(5, "Veteran Student", "Total focused time reached 15 days", "timer",  0, 15, 250),
            Achievement(6, "Master Student", "Total focused time reached 30 days", "timer",  0, 30, 300),
            Achievement(7, "Collector???", "Collect 3 items", "item",  0, 3, 100),
            Achievement(8, "Enthusiastic Collector", "Collect 6 items", "item",  0, 6, 200),
            Achievement(9, "The Collector", "Collect 9 items", "item",  0, 9,300 ),
        )
    }
    fun pullGacha(times: Int): List<ShopItem> {
        val pulledItems = mutableListOf<ShopItem>()
        repeat(times) {
            val pulledItem = getRandomGachaItem()
            updateGachaItems(pulledItem)
            pulledItems.add(pulledItem)
        }
        return pulledItems
    }
    private fun getRandomGachaItem(): ShopItem {
        val totalWeight = _gachaItems.value?.sumOf { it.weight } ?: 0
        var randomWeight = Random.nextInt(totalWeight)
        for (item in _gachaItems.value.orEmpty()) {
            if (randomWeight < item.weight) {
                return item
            } else {
                randomWeight -= item.weight
            }
        }
        return ShopItem(0,"Default Item", Rarity.COMMON, 9, 0)
    }

    private fun updateGachaItems(newItem: ShopItem) {
        val currentItems = _gachaItems.value.orEmpty().toMutableList()
        val existingItem = currentItems.find { it.name == newItem.name }
        if (existingItem != null) {
            existingItem.count++
            if (existingItem.count % 5 == 0) {
                existingItem.isCountingUp = true
            }
        } else {
            currentItems.add(newItem)
        }
        val uniqueItems = currentItems.distinctBy { it.name }
        _gachaItems.value = uniqueItems
    }

    fun updateAchievementProgress(achievementId: Int, progress: Int) {
        val currentAchievements = _achievements.value.orEmpty().toMutableList()
        val achievement = currentAchievements.find { it.id == achievementId }
        if (achievement != null) {
            achievement.updateStatus(progress)
            achievement.progress =+ progress
        }

        if (achievement?.isCompleted == true) {
            showToast("Achievement cleared: ${achievement.name}")
        }
        _achievements.value = currentAchievements
    }

    fun updateTimerAchievement(achievementId: Int,timerMinutes: Int) {
        val totalHours = timerMinutes / 60
        val currentUsage = _totalTimerUsage.value ?: 0
        _totalTimerUsage.value = currentUsage + totalHours
        val totalUsage = _totalTimerUsage.value ?: 0
        val achievements = _achievements.value.orEmpty().toMutableList()
        val timerProAchievement = achievements.find { it.id == achievementId }
        if(achievementId == 2){
            if (timerProAchievement != null && !timerProAchievement.isCompleted) {
                timerProAchievement.progress = totalUsage
                if (totalUsage >= timerProAchievement.targetProgress) {
                    timerProAchievement.isCompleted = true
                    timerProAchievement.isCollect = true
                }
                if (timerProAchievement.isCompleted == true) {
                    showToast("Achievement cleared: ${timerProAchievement.name}")
                }
            }
        }else{
            val totaldays = totalUsage/24
            if (timerProAchievement != null && !timerProAchievement.isCompleted) {
                timerProAchievement.progress = totaldays
                if (totaldays >= timerProAchievement.targetProgress) {
                    timerProAchievement.isCompleted = true
                    timerProAchievement.isCollect = true
                }
                if (timerProAchievement.isCompleted == true) {
                    showToast("Achievement cleared: ${timerProAchievement.name}")
                }
            }
        }

        _achievements.value = achievements
    }
    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}