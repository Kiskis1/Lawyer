package com.acruxcs.lawyer.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.WorkingHours
import com.acruxcs.lawyer.repository.CasesRepository
import com.acruxcs.lawyer.repository.UsersRepository
import com.acruxcs.lawyer.utils.Status
import com.crazylegend.kotlinextensions.livedata.SingleLiveEvent
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

    fun uploadImage(uri: Uri) {
        usersRepository.uploadImage(uri, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.PICTURE_CHANGE_SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun postCase(case: Case) {
        if (case.user == "") case.user = firebaseUser!!.uid
        casesRepository.postCase(case).addOnSuccessListener {
            status.value = Status.SUCCESS
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

    fun updateUser(user: User) {
        usersRepository.setUser(user).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateHours(hours: WorkingHours) {
        usersRepository.updateHours(hours, MainActivity.user.value!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }
}
