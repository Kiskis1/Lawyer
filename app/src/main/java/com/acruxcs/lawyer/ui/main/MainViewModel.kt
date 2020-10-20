package com.acruxcs.lawyer.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    private val repository = FirebaseRepository
    val firebaseAuth = Firebase.auth
    var user = MutableLiveData<User>()
    val loggingIn = MutableLiveData<Boolean>().also { it.value = false }

    fun setUser(newUser: User) {
        user.value = newUser
    }

    fun createNewUser(task: Task<AuthResult>) {
        val profile = task.result!!.user
        if (task.result!!.additionalUserInfo!!.isNewUser) {
            val newUser = User(
                email = profile!!.email!!,
                nickname = profile.displayName!!
            )
            user.value = newUser
            repository.writeNewUser(profile.uid, newUser)
        }
    }

    fun setLoggingIn(bool: Boolean) {
        loggingIn.value = bool
    }

    fun createNewUser(user: User) {
        repository.writeNewUser(firebaseAuth.currentUser!!.uid, user)
    }

    fun getCurrentUser() = firebaseAuth.currentUser
}