package com.acruxcs.lawyer.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.SingleLiveEvent
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private val repository = FirebaseRepository
    private val firebaseUser = Firebase.auth.currentUser

    private val status = SingleLiveEvent<MainViewModel.Companion.Status>()

    fun getStatus(): SingleLiveEvent<MainViewModel.Companion.Status> {
        return status
    }

    fun updateCountry(country: String) {
        repository.updateCountry(country, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }

    fun updateCity(city: String) {
        repository.updateCity(city, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }

    fun updatePhone(phone: String) {
        repository.updatePhone(phone, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }

    fun updatePassword(password: String) {
        firebaseUser!!.updatePassword(password).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthRecentLoginRequiredException -> status.value =
                    MainViewModel.Companion.Status.REAUTHENTICATE
                is FirebaseNetworkException -> status.value =
                    MainViewModel.Companion.Status.NO_NETWORK
                else -> status.value = MainViewModel.Companion.Status.ERROR
            }
        }
    }

    fun uploadImage(uri: Uri) {
        repository.uploadImage(uri, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.PICTURE_CHANGE_SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }

    fun updateWonCases(wonCases: Int) {
        repository.updateWonCases(wonCases, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }

    fun updateExperience(experience: Int) {
        repository.updateExperience(experience, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }

    fun updateEducation(education: String) {
        repository.updateEducation(education, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }

    fun updateSpecialization(specialization: String) {
        repository.updateSpecialization(specialization, firebaseUser!!.uid).addOnSuccessListener {
            status.value = MainViewModel.Companion.Status.SUCCESS
        }.addOnFailureListener {
            status.value = MainViewModel.Companion.Status.ERROR
        }
    }
}
