package com.acruxcs.lawyer.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Case
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

    fun updateCountry(country: String) {
        usersRepository.updateCountry(country, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateCity(city: String) {
        usersRepository.updateCity(city, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updatePhone(phone: String) {
        usersRepository.updatePhone(phone, firebaseUser!!.uid).addOnSuccessListener {
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
        usersRepository.uploadImage(uri, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.PICTURE_CHANGE_SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateWonCases(wonCases: Int) {
        usersRepository.updateWonCases(wonCases, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateExperience(experience: Int) {
        usersRepository.updateExperience(experience, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateEducation(education: String) {
        usersRepository.updateEducation(education, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateSpecialization(specialization: String) {
        usersRepository.updateSpecialization(specialization, firebaseUser!!.uid)
            .addOnSuccessListener {
                status.value = Status.SUCCESS
            }.addOnFailureListener {
                status.value = Status.ERROR
            }
    }

    fun postCase(case: Case) {
        case.user = firebaseUser!!.uid
        casesRepository.postCase(case)
    }

    fun getImageRef(uid: String, ic: ImageCallback) {
        val reference = usersRepository.getImageRef(uid)
        reference.downloadUrl.addOnSuccessListener {
            ic.onCallback(it.toString())
        }.addOnFailureListener {
            ic.onCallback(usersRepository.defaultPicture)
        }
    }

    companion object {
        interface ImageCallback {
            fun onCallback(value: String)
        }
    }
}
