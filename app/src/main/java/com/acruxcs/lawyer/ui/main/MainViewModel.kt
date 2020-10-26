package com.acruxcs.lawyer.ui.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.SingleLiveEvent
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository
    val firebaseAuth = Firebase.auth
    private val firebaseUser = Firebase.auth.currentUser!!
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
                nickname = profile.displayName!!,
                uid = profile.uid
            )
            user.value = newUser
            repository.writeNewUser(profile.uid, newUser)
        }
    }

    fun createNewUser(user: User) {
        user.uid = firebaseUser.uid
        repository.writeNewUser(firebaseUser.uid, user)
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun updatePassword(password: String) {
        firebaseUser.updatePassword(password).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthRecentLoginRequiredException -> status.value = Status.REAUTHENTICATE
                is FirebaseNetworkException -> status.value = Status.NO_NETWORK
                else -> status.value = Status.ERROR
            }

        }
    }

    fun uploadImage(uri: Uri) {
        repository.uploadImage(uri, firebaseUser.uid).addOnSuccessListener {
            status.value = Status.PICTURE_CHANGE_SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun getImageRef(uid: String, ic: ImageCallback) {
        val reference = repository.getImageRef(uid)
        reference.downloadUrl.addOnSuccessListener {
            ic.onCallback(reference)
        }.addOnFailureListener {
            ic.onCallback(repository.getDefaultImageRef())
        }
    }

    fun getStatus(): SingleLiveEvent<Status> {
        return status
    }

    fun postCase(case: Case) {
        case.user = firebaseUser.uid
        repository.postCase(case)
    }

    companion object {
        interface ImageCallback {
            fun onCallback(value: StorageReference)
        }

        enum class Status {
            SUCCESS,
            PICTURE_CHANGE_SUCCESS,
            ERROR,
            REAUTHENTICATE,
            NO_NETWORK,
        }
    }
}

