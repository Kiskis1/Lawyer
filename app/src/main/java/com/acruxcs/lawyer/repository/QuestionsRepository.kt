package com.acruxcs.lawyer.repository

import com.acruxcs.lawyer.model.Question
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object QuestionsRepository {
    private val db = Firebase.database.reference

    fun postQuestion(question: Question) = db.child("questions").push().setValue(question)

    //gauti advokatui uzduotus klausimus
    fun getAskedQuestions(email: String) =
        db.child("questions").orderByChild("destinationEmail").equalTo(email)

    //gauti naudotojo uzduotus klausimus
    fun getSentQuestions(email: String) =
        db.child("questions").orderByChild("sender").equalTo(email)
}
