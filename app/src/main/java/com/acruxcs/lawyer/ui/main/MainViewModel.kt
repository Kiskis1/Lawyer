package com.acruxcs.lawyer.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.SingleLiveEvent
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository
    val firebaseAuth = Firebase.auth
    var user = MutableLiveData<User>()
    val loggedIn = MutableLiveData<Boolean>().also { it.value = false }

    private val status = SingleLiveEvent<Status>()

    fun setUser(newUser: User) {
        user.value = newUser
    }

    fun setLoggedIn(bool: Boolean) {
        loggedIn.value = bool
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

    fun createNewUser(user: User) {
        repository.writeNewUser(firebaseAuth.currentUser!!.uid, user)
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun updatePassword(password: String) {
        val user = firebaseAuth.currentUser!!
        user.updatePassword(password).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthRecentLoginRequiredException -> Status.REAUTHENTICATE
                is FirebaseNetworkException -> Status.NO_NETWORK
                else -> Status.ERROR
            }

        }
    }

    fun getStatus(): SingleLiveEvent<Status> {
        return status
    }

    companion object {
        enum class Status {
            SUCCESS,
            ERROR,
            REAUTHENTICATE,
            NO_NETWORK,
            INVALID_URI
        }
    }
}
