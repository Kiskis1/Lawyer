package lt.viko.eif.lawyer.ui.main.reservation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazylegend.kotlinextensions.livedata.SingleLiveEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import lt.viko.eif.lawyer.model.Reservation
import lt.viko.eif.lawyer.repository.ReservationsRepository
import lt.viko.eif.lawyer.utils.Status
import lt.viko.eif.lawyer.utils.Utils
import java.util.Date

class ReservationsViewModel : ViewModel() {

    private val db = ReservationsRepository

    private val userReservations = MutableLiveData<List<Reservation>>()
    private val lawyerReservations = MutableLiveData<List<Reservation>>()

    private val status = SingleLiveEvent<Status>()

    fun getStatus() = status

    fun getReservationsForUser(uid: String): MutableLiveData<List<Reservation>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Reservation>()
                for (reservation in snapshot.children) {
                    reservation.getValue(Reservation::class.java)?.let { list.add(it) }
                }
                list.sortWith(compareByDescending<Reservation> { it.date }.thenBy { it.time })
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
                    val item = reservation.getValue(Reservation::class.java)
                    val strDate = Utils.dateFormat.parse("${item!!.date} ${item.time}")
                    if (!Date().after(strDate)) {
                        list.add(item)
                    }
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

    fun postReservation(reservation: Reservation) {
        db.postReservation(reservation).addOnCompleteListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }
}
