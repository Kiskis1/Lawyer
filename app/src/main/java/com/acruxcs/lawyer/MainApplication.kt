package com.acruxcs.lawyer

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.acruxcs.lawyer.model.User

class MainApplication : Application() {

    companion object {
        val user = MutableLiveData<User>()
        val loggedIn = MutableLiveData<Boolean>().also { it.value = false }
    }
}
