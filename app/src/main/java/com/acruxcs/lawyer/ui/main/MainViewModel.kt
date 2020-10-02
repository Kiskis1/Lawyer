package com.acruxcs.lawyer.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.FirebaseRepository
import com.acruxcs.lawyer.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName

    private val repository = FirebaseRepository
    val firebaseAuth = Firebase.auth
    private var user = MutableLiveData<User>()

    fun getUserData(userId: String): MutableLiveData<User> {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user.value = snapshot.getValue(User::class.java)
                println(user.value)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        repository.getUser(userId)
            .addValueEventListener(userListener)

        return user
    }
}