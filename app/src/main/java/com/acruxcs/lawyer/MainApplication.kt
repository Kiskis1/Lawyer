package com.acruxcs.lawyer

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.acruxcs.lawyer.model.User

class MainApplication : Application() {

    companion object {
        val user = MutableLiveData<User>()
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}
