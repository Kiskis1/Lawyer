package com.acruxcs.lawyer.ui.login

import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginViewModel : ViewModel() {

    private val repository = FirebaseRepository
    val firebaseAuth = Firebase.auth

    fun createNewUser(task: Task<AuthResult>) {
        val profile = task.result!!.user
        if (task.result!!.additionalUserInfo!!.isNewUser) {
            val newUser = User(
                email = profile!!.email!!,
                nickname = profile.displayName!!
            )
            repository.writeNewUser(profile.uid, newUser)
        }
    }

    fun createNewUser(user: User) {
        repository.writeNewUser(firebaseAuth.currentUser!!.uid, user)
    }

    fun getCurrentUser() = firebaseAuth.currentUser
}