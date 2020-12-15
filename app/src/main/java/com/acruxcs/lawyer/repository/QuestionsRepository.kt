package com.acruxcs.lawyer.repository

import com.acruxcs.lawyer.model.Question
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object QuestionsRepository {
    private val db = Firebase.database.reference.child("questions")

    fun postQuestion(question: Question) = db.child(question.id).setValue(question)

    //gauti advokatui uzduotus klausimus
    fun getAskedQuestions(id: String) =
        db.orderByChild("destination").equalTo(id)

    //gauti naudotojo uzduotus klausimus
    fun getSentQuestions(id: String) =
        db.orderByChild("sender").equalTo(id)

    fun deleteQuestion(id: String) = db.child(id).removeValue()
}
