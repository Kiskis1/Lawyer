package com.acruxcs.lawyer

import androidx.lifecycle.MutableLiveData
import com.acruxcs.lawyer.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseRepository {
    private val db = Firebase.database.reference

    fun writeNewUser(userId: String, user: User) {
        db.child("users").child(userId).setValue(user)
    }

    fun getUserCollection() = db.child("users")
}