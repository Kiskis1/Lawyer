package com.acruxcs.lawyer.ui.lawyers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class LawyersViewModel : ViewModel() {
    private val TAG = this::class.java.simpleName
    private val repository = FirebaseRepository
    private var lawyers = MutableLiveData<List<Lawyer>>()

    fun getLawyers(): MutableLiveData<List<Lawyer>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Lawyer>()
                for (lawyer in snapshot.children) {
                    lawyer.getValue(Lawyer::class.java)?.let { list.add(it) }
                }
                lawyers.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        repository.getLawyers().addValueEventListener(listener)
        return lawyers
    }
}