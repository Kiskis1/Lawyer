package com.acruxcs.lawyer.ui.main.reservation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.repository.ReservationsRepository
import com.acruxcs.lawyer.utils.Status
import com.crazylegend.kotlinextensions.livedata.SingleLiveEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ReservationsViewModel : ViewModel() {

    private val db = ReservationsRepository

    private val userReservations = MutableLiveData<List<Reservation>>()
    private val lawyerReservations = MutableLiveData<List<Reservation>>()

    private val status = SingleLiveEvent<Status>()

    fun getStatus(): SingleLiveEvent<Status> {
        return status
    }

    fun getReservationsForUser(uid: String): MutableLiveData<List<Reservation>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Reservation>()
                for (reservation in snapshot.children) {
                    reservation.getValue(Reservation::class.java)?.let { list.add(it) }
                }
                list.sortWith(compareBy<Reservation> { it.date }.thenBy { it.time })
                userReservations.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        db.getReservationsForUser(uid).addValueEventListener(listener)
        return userReservations
    }

    fun getReservationsForLawyer(uid: String): MutableLiveData<List<Reservation>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Reservation>()
                for (reservation in snapshot.children) {
                    reservation.getValue(Reservation::class.java)?.let { list.add(it) }
                }
                list.sortWith(compareBy<Reservation> { it.date }.thenBy { it.time })
                lawyerReservations.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        db.getReservationsForLawyer(uid).addValueEventListener(listener)
        return lawyerReservations
    }

    fun deleteReservation(id: String) {
        db.deleteReservation(id).addOnCompleteListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun createReservation(reservation: Reservation) {
        db.createReservation(reservation).addOnCompleteListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }
}
