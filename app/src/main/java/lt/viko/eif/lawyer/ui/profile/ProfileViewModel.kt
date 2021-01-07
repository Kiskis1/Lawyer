package lt.viko.eif.lawyer.ui.profile

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crazylegend.kotlinextensions.livedata.SingleLiveEvent
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.model.Case
import lt.viko.eif.lawyer.model.Reservation
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.model.WorkingHours
import lt.viko.eif.lawyer.repository.CasesRepository
import lt.viko.eif.lawyer.repository.ReservationsRepository
import lt.viko.eif.lawyer.repository.UsersRepository
import lt.viko.eif.lawyer.utils.Status
import lt.viko.eif.lawyer.utils.Utils
import java.util.Date

class ProfileViewModel : ViewModel() {
    private val usersRepository = UsersRepository
    private val casesRepository = CasesRepository
    val firebaseUser = Firebase.auth.currentUser
    private val lawyerPreviousReservations = MutableLiveData<List<Reservation>>()

    private val status = SingleLiveEvent<Status>()

    fun getStatus(): SingleLiveEvent<Status> {
        return status
    }

    fun updatePassword(password: String) {
        firebaseUser!!.updatePassword(password).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            when (it) {
                is FirebaseAuthRecentLoginRequiredException -> status.value =
                    Status.REAUTHENTICATE
                is FirebaseNetworkException -> status.value =
                    Status.NO_NETWORK
                else -> status.value = Status.ERROR
            }
        }
    }

    fun uploadImage(uri: Uri) {
        usersRepository.uploadImage(uri, firebaseUser!!.uid).addOnSuccessListener {
            status.value = Status.PICTURE_CHANGE_SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun postCase(case: Case) {
        if (case.user == "") case.user = firebaseUser!!.uid
        casesRepository.postCase(case).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun deleteCase(id: String) {
        casesRepository.deleteCase(id).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateUser(user: User) {
        usersRepository.setUser(user).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun updateHours(hours: WorkingHours) {
        usersRepository.updateHours(hours, MainActivity.user.value!!.uid).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
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
}
