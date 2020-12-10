package com.acruxcs.lawyer.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.CasesRepository
import com.acruxcs.lawyer.repository.UsersRepository
import com.acruxcs.lawyer.utils.SingleLiveEvent
import com.acruxcs.lawyer.utils.Status
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private val usersRepository = UsersRepository
    private val casesRepository = CasesRepository
    private val firebaseUser = Firebase.auth.currentUser

    private val status = SingleLiveEvent<Status>()

    fun getStatus(): SingleLiveEvent<Status> {
        return status
    }

    fun updatePassword(password: String) {
        firebaseUser!!.updatePassword(password).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthRecentLoginRequiredException -> status.value =
                    Status.REAUTHENTICATE
                is FirebaseNetworkException -> status.value =
                    Status.NO_NETWORK
                else -> status.value = Status.ERROR
            }
        }
    }

    fun uploadImage(uri: Uri, callback: (result: Int) -> Unit) {
        usersRepository.uploadImage(uri, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.PICTURE_CHANGE_SUCCESS
            callback(0)
        }.addOnFailureListener {
            status.value = Status.ERROR
            callback(1)
        }
    }

    fun postCase(case: Case) {
        if (case.user == "") case.user = firebaseUser!!.uid
        casesRepository.postCase(case).addOnSuccessListener {
            status.value = Status.UPDATE_SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun deleteCase(id: String) {
        casesRepository.deleteCase(id).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun getImageRef(uid: String, ic: ImageCallback) {
        val reference = usersRepository.getImageRef(uid)
        reference.downloadUrl.addOnSuccessListener {
            ic.onCallback(it.toString())
        }.addOnFailureListener {
            ic.onCallback(usersRepository.defaultPicture)
        }
    }

    fun updateUser(user: User) {
        usersRepository.updateUser(user).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    companion object {
        interface ImageCallback {
            fun onCallback(value: String)
        }
    }
}
