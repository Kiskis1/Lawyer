package com.acruxcs.lawyer.ui.lawyers

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.function.Predicate
import java.util.stream.Collectors

class LawyersViewModel : ViewModel() {
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

    @SuppressLint("NewApi")
    fun filter(list: List<Lawyer>, filter: MutableList<Predicate<Lawyer>>): List<Lawyer> {
        if (filter.size == 0) return listOf()
        val composite = filter.stream()
            .reduce({ _ -> true }) { p1, p2 ->
                p1.and(p2)
            }
        return list.stream().filter(composite).collect(Collectors.toList())
    }
}
