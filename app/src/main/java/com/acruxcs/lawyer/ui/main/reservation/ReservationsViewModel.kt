package com.acruxcs.lawyer.ui.main.reservation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.repository.UsersRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ReservationsViewModel : ViewModel() {

    private val db = UsersRepository

    private val userReservations = MutableLiveData<List<Reservation>>()
    private val lawyerReservations = MutableLiveData<List<Reservation>>()

    fun getReservationsForUser(uid: String): MutableLiveData<List<Reservation>> {

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Reservation>()
                for (reservation in snapshot.children) {
                    reservation.getValue(Reservation::class.java)?.let { list.add(it) }
                }
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
                lawyerReservations.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        db.getReservationsForLawyer(uid).addValueEventListener(listener)
        return lawyerReservations
    }
}
