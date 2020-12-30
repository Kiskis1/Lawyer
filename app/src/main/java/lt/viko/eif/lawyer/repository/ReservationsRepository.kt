package lt.viko.eif.lawyer.repository

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import lt.viko.eif.lawyer.model.Reservation

object ReservationsRepository {
    private val db = Firebase.database.reference.child("reservations")

    fun deleteReservation(id: String) = db.child(id).removeValue()

    fun postReservation(res: Reservation) = db.child(res.id).setValue(res)

    fun getReservationsForUser(uid: String) =
        db.orderByChild("user").equalTo(uid)

    fun getReservationsForLawyer(uid: String) =
        db.orderByChild("lawyer/uid").equalTo(uid)

    fun getLawyersReservations(dateLawyer: String) =
        db.orderByChild("dateLawyer").equalTo(dateLawyer)
}
