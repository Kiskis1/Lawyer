package com.acruxcs.lawyer.ui.main.askedquestions

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.repository.QuestionsRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class QuestionsViewModel : ViewModel() {
    private val questionsRepository = QuestionsRepository

    //advokatui
    private val askedQuestions = MutableLiveData<List<Question>>()

    //naudotojo
    private val sentQuestions = MutableLiveData<List<Question>>()

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
}
