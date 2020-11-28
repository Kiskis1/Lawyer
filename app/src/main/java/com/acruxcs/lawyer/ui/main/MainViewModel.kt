package com.acruxcs.lawyer.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.CasesRepository
import com.acruxcs.lawyer.repository.QuestionsRepository
import com.acruxcs.lawyer.repository.UsersRepository
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.edit
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class MainViewModel : ViewModel() {
    private val usersRepository = UsersRepository
    private val casesRepository = CasesRepository
    private val questionsRepository = QuestionsRepository
    val firebaseAuth = Firebase.auth
    val firebaseUser = Firebase.auth.currentUser
    var user = MutableLiveData<User>()
    val loggedIn = MutableLiveData<Boolean>().also { it.value = false }

    //advokatui
    private val askedQuestions = MutableLiveData<List<Question>>()

    //naudotojo
    private val sentQuestions = MutableLiveData<List<Question>>()

    fun setUser(newUser: User) {
        user.value = newUser
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
            usersRepository.writeNewUser(newUser)
        }
    }

    fun getUserData(userId: String?) {
        val userListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temp = snapshot.getValue(User::class.java)
                user.postValue(temp)
                Utils.preferences
                    .edit { it.putString(Utils.SHARED_USER_DATA, Gson().toJson(temp)) }
                setLoggedIn(true)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        usersRepository.getUser(userId)
            ?.addValueEventListener(userListener)
    }

    fun createNewUser(newUser: User) {
        user.value = newUser
        usersRepository.writeNewUser(newUser)
    }

    fun getImageRef(uid: String, ic: ImageCallback) {
        val reference = usersRepository.getImageRef(uid)
        reference.downloadUrl.addOnSuccessListener {
            ic.onCallback(it.toString())
        }.addOnFailureListener {
            ic.onCallback(usersRepository.defaultPicture)
        }
    }

    fun postCase(case: Case) {
        case.user = firebaseUser!!.uid
        casesRepository.postCase(case)
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
        questionsRepository.getAskedQuestions(email).addValueEventListener(listener)
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
        questionsRepository.getSentQuestions(email).addValueEventListener(listener)
        return sentQuestions
    }

    companion object {
        interface ImageCallback {
            fun onCallback(value: String)
        }
    }
}
