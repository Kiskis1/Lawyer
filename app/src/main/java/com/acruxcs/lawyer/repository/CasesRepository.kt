package com.acruxcs.lawyer.repository

import com.acruxcs.lawyer.model.Case
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object CasesRepository {
    private val db = Firebase.database.reference

    fun postCase(case: Case) = db.child("cases").child(case.id).setValue(case)

    fun getLawyersCases(uid: String) = db.child("cases").orderByChild("user").equalTo(uid)

    fun deleteCase(id: String) = db.child("cases").child(id).removeValue()
}
