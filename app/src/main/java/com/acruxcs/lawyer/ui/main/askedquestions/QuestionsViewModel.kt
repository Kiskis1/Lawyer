package com.acruxcs.lawyer.ui.main.askedquestions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.repository.QuestionsRepository
import com.acruxcs.lawyer.utils.Status
import com.crazylegend.kotlinextensions.livedata.SingleLiveEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class QuestionsViewModel : ViewModel() {
    private val db = QuestionsRepository

    private val askedQuestions = MutableLiveData<List<Question>>()

    private val status = SingleLiveEvent<Status>()

    fun getStatus(): SingleLiveEvent<Status> {
        return status
    }

    //advokatui uzduoti klaus
    fun getAskedQuestions(id: String): MutableLiveData<List<Question>> {
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
        db.getAskedQuestions(id).addValueEventListener(listener)
        return askedQuestions
    }

    //naudotojo uzduoti klaus
    fun getSentQuestions(id: String): MutableLiveData<List<Question>> {
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
        db.getSentQuestions(id).addValueEventListener(listener)
        return askedQuestions
    }

    fun deleteQuestion(id: String) {
        db.deleteQuestion(id).addOnCompleteListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    fun postQuestion(question: Question) {
        db.postQuestion(question).addOnCompleteListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }
}
