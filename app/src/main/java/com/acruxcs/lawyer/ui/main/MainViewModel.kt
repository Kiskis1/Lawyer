package com.acruxcs.lawyer.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.AppUser
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.edit
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson

class MainViewModel : ViewModel() {
    private val repository = FirebaseRepository
    val firebaseAuth = Firebase.auth
    val firebaseUser = Firebase.auth.currentUser
    var user = MutableLiveData<AppUser>()
    var lawyer = MutableLiveData<Lawyer>()
    val loggedIn = MutableLiveData<Boolean>().also { it.value = false }

    //advokatui
    private val askedQuestions = MutableLiveData<List<Question>>()

    //naudotojo
    private val sentQuestions = MutableLiveData<List<Question>>()

    fun setUser(newUser: AppUser) {
        user.value = newUser
    }

    fun setLawyer(l: Lawyer) {
        lawyer.value = l
    }

    fun setLoggedIn(bool: Boolean) {
        loggedIn.value = bool
    }

    fun createNewUser(task: Task<AuthResult>) {
        val profile = task.result!!.user
        if (task.result!!.additionalUserInfo!!.isNewUser) {
            val newUser = User(
                email = profile!!.email!!,
                fullname = profile.displayName!!,
                uid = profile.uid
            )
            user.value = newUser
            repository.writeNewUser(newUser)
        }
    }

    fun getUserData(userId: String?) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(User::class.java)
                if (temp?.role == "user") {
                    user.postValue(temp)
                    Utils.preferences
                        .edit { it.putString(Utils.SHARED_USER_DATA, Gson().toJson(temp)) }
                    setLoggedIn(true)
                } else {
                    val lawyerFromDB = snapshot.getValue(Lawyer::class.java)
                    user.postValue(lawyerFromDB)
                    lawyer.postValue(lawyerFromDB)
                    Utils.preferences
                        .edit { it.putString(Utils.SHARED_USER_DATA, Gson().toJson(lawyerFromDB)) }
                    setLoggedIn(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        FirebaseRepository.getUser(userId)
            ?.addValueEventListener(userListener)
    }

    fun createNewUser(newUser: AppUser) {
        user.value = newUser
        repository.writeNewUser(newUser)
    }

    fun getImageRef(uid: String, ic: ImageCallback) {
        val reference = repository.getImageRef(uid)
        reference.downloadUrl.addOnSuccessListener {
            ic.onCallback(reference)
        }.addOnFailureListener {
            ic.onCallback(repository.getDefaultImageRef())
        }
    }

    fun postCase(case: Case) {
        case.user = firebaseUser!!.uid
        repository.postCase(case)
    }

    //advokatui uzduoti klaus
    fun getAskedQuestions(email: String): MutableLiveData<List<Question>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Question>()
                for (question in snapshot.children) {
                    question.getValue(Question::class.java)?.let { list.add(it) }
                }
                askedQuestions.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        repository.getAskedQuestions(email).addValueEventListener(listener)
        return askedQuestions
    }

    //naudotojo uzduoti klaus
    fun getSentQuestions(email: String): MutableLiveData<List<Question>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Question>()
                for (question in snapshot.children) {
                    question.getValue(Question::class.java)?.let { list.add(it) }
                }
                sentQuestions.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        repository.getSentQuestions(email).addValueEventListener(listener)
        return sentQuestions
    }

    companion object {
        interface ImageCallback {
            fun onCallback(value: StorageReference)
        }

        enum class Status {
            SUCCESS,
            PICTURE_CHANGE_SUCCESS,
            ERROR,
            REAUTHENTICATE,
            NO_NETWORK,
        }
    }
}
