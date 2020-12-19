package com.acruxcs.lawyer.ui.lawyers

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.QuestionsRepository
import com.acruxcs.lawyer.repository.ReservationsRepository
import com.acruxcs.lawyer.repository.UsersRepository
import com.acruxcs.lawyer.utils.Status
import com.crazylegend.kotlinextensions.livedata.SingleLiveEvent
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.function.Predicate
import java.util.stream.Collectors

class LawyersViewModel : ViewModel() {
    private val usersRepository = UsersRepository
    private val questionsRepository = QuestionsRepository
    private val reservationsRepository = ReservationsRepository
    private val lawyers = MutableLiveData<List<User>>()
    private val availableTimes = MutableLiveData<List<LocalTime>>()

    private val regex = "(\\d+):(\\d+)".toRegex()

    private val status = SingleLiveEvent<Status>()

    fun getStatus() = status

    fun getLawyers(): MutableLiveData<List<User>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<User>()
                for (lawyer in snapshot.children) {
                    lawyer.getValue(User::class.java)?.let { list.add(it) }
                }
                lawyers.postValue(list)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }
        usersRepository.getLawyers().addValueEventListener(listener)
        return lawyers
    }

    fun createReservation(res: Reservation) {
        reservationsRepository.postReservation(res).addOnSuccessListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    @SuppressLint("NewApi")
    fun filter(list: List<User>, filter: MutableList<Predicate<User>>): List<User> {
        if (filter.size == 0) return listOf()
        val composite = filter.stream()
            .reduce({ true }) { p1, p2 ->
                p1.and(p2)
            }
        return list.stream().filter(composite).collect(Collectors.toList())
    }

    fun postQuestion(question: Question) {
        questionsRepository.postQuestion(question).addOnCompleteListener {
            status.value = Status.SUCCESS
        }.addOnFailureListener {
            status.value = Status.ERROR
        }
    }

    private fun getTimeRange(range: String): MutableList<LocalTime> {
        if (range == "") return mutableListOf()
        val timeList = mutableListOf<LocalTime>()
        val matches = regex.findAll(range)
        matches.forEach {
            val str = DateTimeFormatter.ofPattern("HH:mm")
            val time = LocalTime.parse(it.value, str)
            timeList.add(time)
        }
        val list = mutableListOf<LocalTime>()
        var time: LocalTime = timeList[0]
        while (time.isBefore(timeList[1])) {
            list.add(time)
            time = time.plusHours(1)
        }
        return list
    }

    fun getAvailableTimes(date: String, dayOfWeek: Int, lawyer: User):
        MutableLiveData<List<LocalTime>> {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Reservation>()
                for (reserv in snapshot.children) {
                    reserv.getValue(Reservation::class.java)?.let { list.add(it) }
                }
                val all = getLawyerWorkingTimes(dayOfWeek, lawyer)
                if (list.isNotEmpty()) {
                    for (item in list) {
                        all.remove(LocalTime.parse(item.time, DateTimeFormatter.ofPattern("HH:mm")))
                    }
                }
                availableTimes.postValue(all)
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        }

        val dateLawyer = "${date}_${lawyer.uid}"
        reservationsRepository.getLawyersReservations(dateLawyer)
            .addListenerForSingleValueEvent(listener)
        return availableTimes
    }

    private fun getLawyerWorkingTimes(dayOfWeek: Int, lawyer: User): MutableList<LocalTime> {
        var range: MutableList<LocalTime> = mutableListOf()
        when (dayOfWeek) {
            1 -> {
                range = getTimeRange(lawyer.workingHours!!.sunday)
            }
            2 -> {
                range = getTimeRange(lawyer.workingHours!!.monday)
            }
            3 -> {
                range = getTimeRange(lawyer.workingHours!!.tuesday)
            }
            4 -> {
                range = getTimeRange(lawyer.workingHours!!.wednesday)
            }
            5 -> {
                range = getTimeRange(lawyer.workingHours!!.thursday)
            }
            6 -> {
                range = getTimeRange(lawyer.workingHours!!.friday)
            }
            7 -> {
                range = getTimeRange(lawyer.workingHours!!.saturday)
            }
        }
        return range
    }
}
