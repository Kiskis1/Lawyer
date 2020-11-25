package com.acruxcs.lawyer.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.SingleLiveEvent
import com.acruxcs.lawyer.utils.Status
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository
    private val firebaseUser = Firebase.auth.currentUser

    private val status = SingleLiveEvent<Status>()

    fun getStatus(): SingleLiveEvent<Status> {
        return status
    }

    fun updateCountry(country: String) {
        repository.updateCountry(country, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateCity(city: String) {
        repository.updateCity(city, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updatePhone(phone: String) {
        repository.updatePhone(phone, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
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
        repository.uploadImage(uri, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.PICTURE_CHANGE_SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateWonCases(wonCases: Int) {
        repository.updateWonCases(wonCases, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateExperience(experience: Int) {
        repository.updateExperience(experience, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateEducation(education: String) {
        repository.updateEducation(education, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateSpecialization(specialization: String) {
        repository.updateSpecialization(specialization, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }
}
