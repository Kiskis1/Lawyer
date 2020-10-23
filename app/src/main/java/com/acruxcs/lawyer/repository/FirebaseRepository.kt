package com.acruxcs.lawyer.repository

import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseRepository {
    private val db = Firebase.database.reference

    fun writeNewUser(userId: String, user: User) = db.child("users").child(userId).setValue(user)

    fun getUser(userId: String?) = userId?.let { db.child("users").child(it) }

    fun postQuestion(question: Question) = db.child("questions").push().setValue(question)

    fun getLawyers() = db.child("users").orderByChild("role").equalTo("lawyer")

    fun postCase(case: Case) = db.child("cases").push().setValue(case)
}
