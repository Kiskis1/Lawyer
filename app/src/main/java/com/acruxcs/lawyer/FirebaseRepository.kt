package com.acruxcs.lawyer

import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseRepository {
    private val db = Firebase.database.reference

    fun writeNewUser(userId: String, user: User) = db.child("users").child(userId).setValue(user)

    fun getUserCollection() = db.child("users")

    fun getUser(userId: String) = db.child("users").child(userId)

    fun sendQuestion(question: Question) = db.child("questions").push().setValue(question)
}