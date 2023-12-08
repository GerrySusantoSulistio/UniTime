package com.example.myapplication.Achievement

data class Achievement(
    val id: Int,
    val name: String,
    val description: String,
    val type: String,
    var progress: Int,
    val targetProgress: Int,
    val reward:Int,
    var isCompleted: Boolean = false,
    var isCollect: Boolean = false,
) {
    fun updateStatus(progress: Int) {
        if (!isCompleted && progress >= targetProgress) {
            isCompleted = true
            isCollect=true
        }
    }
}
