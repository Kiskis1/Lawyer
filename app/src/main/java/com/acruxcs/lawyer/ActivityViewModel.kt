package com.acruxcs.lawyer

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.CasesRepository
import com.acruxcs.lawyer.repository.ReservationsRepository
import com.acruxcs.lawyer.repository.SharedPrefRepository
import com.acruxcs.lawyer.repository.SharedPrefRepository.edit
import com.acruxcs.lawyer.repository.UsersRepository
import com.acruxcs.lawyer.utils.Utils
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Date

class ActivityViewModel : ViewModel() {
    private val cases = MutableLiveData<List<Case>>()
    private val lawyerPreviousReservations = MutableLiveData<List<Reservation>>()

    fun getLawyersCases(uid: String): MutableLiveData<List<Case>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Case>()
                for (case in snapshot.children) {
                    case.getValue(Case::class.java)?.let { list.add(it) }
                }
                cases.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        CasesRepository.getLawyersCases(uid).addValueEventListener(listener)
        return cases
    }

    fun getUserData(userId: String?) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(User::class.java)
                SharedPrefRepository.preferences
                    .edit {
                        it.putBoolean(SharedPrefRepository.SHARED_LOGGED_IN, true)
                    }
                MainActivity.user.postValue(temp)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        UsersRepository.getUser(userId)
            ?.addValueEventListener(userListener)
    }

    fun getPreviousReservationsForLawyer(uid: String): MutableLiveData<List<Reservation>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Reservation>()
                for (reservation in snapshot.children) {
                    val item = reservation.getValue(Reservation::class.java)
                    val strDate = Utils.dateFormat.parse("${item!!.date} ${item.time}")
                    if (Date().after(strDate)) {
                        list.add(item)
                    }
                }
                list.sortWith(compareBy<Reservation> { it.date }.thenBy { it.time })
                lawyerPreviousReservations.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        ReservationsRepository.getReservationsForLawyer(uid).addValueEventListener(listener)
        return lawyerPreviousReservations
    }

    private val _bottomNavigationVisibility = MutableLiveData<Int>()
    val bottomNavigationVisibility: LiveData<Int>
        get() = _bottomNavigationVisibility

    init {
        showBottomNav()
    }

    fun showBottomNav() {
        _bottomNavigationVisibility.postValue(View.VISIBLE)
    }

    fun hideBottomNav() {
        _bottomNavigationVisibility.postValue(View.GONE)
    }
}
