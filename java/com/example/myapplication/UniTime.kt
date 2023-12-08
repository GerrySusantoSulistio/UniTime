package com.example.myapplication

import android.app.Application
import androidx.lifecycle.ViewModelStore
class UniTime: Application() {
    lateinit var appViewModelStore: ViewModelStore
        private set
    override fun onCreate() {
        super.onCreate()
        appViewModelStore = ViewModelStore()
    }
}
