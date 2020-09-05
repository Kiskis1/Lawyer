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
    private var user = MutableLiveData<User>()

    fun getUserData(): MutableLiveData<User> {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user.value = snapshot.getValue(User::class.java)
                println(user.value)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        FirebaseRepository.getUserCollection().child(Firebase.auth.currentUser!!.uid)
            .addListenerForSingleValueEvent(userListener)
        return user
    }

}