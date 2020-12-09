package com.acruxcs.lawyer.ui.login

import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_LOGGED_IN
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_USER_DATA
import com.acruxcs.lawyer.repository.SharedPrefRepository.edit
import com.acruxcs.lawyer.repository.SharedPrefRepository.preferences
import com.acruxcs.lawyer.repository.UsersRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

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
            MainApplication.user.postValue(newUser)
            usersRepository.writeNewUser(newUser)
        }
    }

    fun createNewUser(newUser: User) {
        MainApplication.user.postValue(newUser)
        usersRepository.writeNewUser(newUser)
    }

    fun getUserData(userId: String?) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(User::class.java)
                MainApplication.user.postValue(temp)
                preferences
                    .edit {
                        it.putString(SHARED_USER_DATA, Gson().toJson(temp))
                        it.putBoolean(SHARED_LOGGED_IN, true)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        usersRepository.getUser(userId)
            ?.addValueEventListener(userListener)
    }
}
