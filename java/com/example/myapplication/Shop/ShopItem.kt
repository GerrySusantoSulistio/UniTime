package com.example.myapplication.Shop

data class ShopItem(val index: Int,
                     val name: String,
                     val rarity: Rarity,
                     val weight: Int,
                     var power: Int,
                     var count: Int = 0,
                     var level: Int = 1,
                     var isEquipped: Boolean = false,
                     var isEquipButtonEnabled: Boolean = true,
                     var isCountingUp: Boolean = false,
                     ){
    fun shouldItemBeVisible(): Boolean {
        return level > 1 || count > 0
    }
}
enum class Rarity() {
    COMMON,
    RARE,
    EPIC,
}
