package com.acruxcs.lawyer.ui.main

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.AppUser
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.SingleLiveEvent
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.edit
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository
    val firebaseAuth = Firebase.auth
    val firebaseUser = Firebase.auth.currentUser
    var user = MutableLiveData<AppUser>()
    val loggedIn = MutableLiveData<Boolean>().also { it.value = false }

    private val status = SingleLiveEvent<Status>()

    fun setUser(newUser: AppUser) {
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
                fullname = profile.displayName!!,
                uid = profile.uid
            )
            user.value = newUser
            repository.writeNewUser(newUser)
        }
    }

    fun getUserData(userId: String?): MutableLiveData<AppUser> {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(User::class.java)
                if (temp?.role == "user") {
                    user.value = snapshot.getValue(User::class.java)
                    Utils.preferences
                        .edit { it.putString(Utils.SHARED_USER_DATA, Gson().toJson(user.value)) }
                    setLoggedIn(true)
                } else {
                    user.value = snapshot.getValue(Lawyer::class.java)
                    Utils.preferences
                        .edit { it.putString(Utils.SHARED_USER_DATA, Gson().toJson(user.value)) }
                    setLoggedIn(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        FirebaseRepository.getUser(userId)
            ?.addValueEventListener(userListener)

        return user
    }

    fun createNewUser(newUser: AppUser) {
        user.value = newUser
        repository.writeNewUser(newUser)
    }

    fun updatePassword(password: String) {
        firebaseUser!!.updatePassword(password).addOnSuccessListener {
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
        repository.uploadImage(uri, firebaseUser!!.uid).addOnSuccessListener {
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
        case.user = firebaseUser!!.uid
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
