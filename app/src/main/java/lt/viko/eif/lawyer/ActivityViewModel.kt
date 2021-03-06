package lt.viko.eif.lawyer

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import lt.viko.eif.lawyer.model.Case
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.repository.CasesRepository
import lt.viko.eif.lawyer.repository.SharedPrefRepository
import lt.viko.eif.lawyer.repository.SharedPrefRepository.edit
import lt.viko.eif.lawyer.repository.UsersRepository

class ActivityViewModel : ViewModel() {
    private val cases = MutableLiveData<List<Case>>()

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
