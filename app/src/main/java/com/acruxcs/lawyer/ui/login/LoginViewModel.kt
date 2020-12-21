package com.acruxcs.lawyer.ui.login

import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.UsersRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {
    private val usersRepository = UsersRepository
    val firebaseAuth = Firebase.auth

    fun createNewUser(task: Task<AuthResult>) {
        val profile = task.result!!.user
        if (task.result!!.additionalUserInfo!!.isNewUser) {
            val newUser = User(
                email = profile!!.email!!,
                fullname = profile.displayName!!,
                uid = profile.uid
            )
            MainActivity.user.postValue(newUser)
            usersRepository.setUser(newUser)
        }
    }

    fun createNewUser(newUser: User) {
        MainActivity.user.postValue(newUser)
        usersRepository.setUser(newUser)
    }
}
