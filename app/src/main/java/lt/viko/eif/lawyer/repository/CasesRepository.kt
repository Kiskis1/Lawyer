package lt.viko.eif.lawyer.repository

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import lt.viko.eif.lawyer.model.Case

object CasesRepository {
    private val db = Firebase.database.reference.child("cases")

    fun postCase(case: Case) = db.child(case.id).setValue(case)

    fun getLawyersCases(uid: String) = db.orderByChild("user").equalTo(uid)

    fun deleteCase(id: String) = db.child(id).removeValue()
}
