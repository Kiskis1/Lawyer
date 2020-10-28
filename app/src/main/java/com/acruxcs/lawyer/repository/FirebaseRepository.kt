package com.acruxcs.lawyer.repository

import android.net.Uri
import com.acruxcs.lawyer.model.AppUser
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.Question
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

object FirebaseRepository {
    private val db = Firebase.database.reference
    private val storage = Firebase.storage.reference.child("images")

    fun writeNewUser(user: AppUser) = db.child("users").child(user.uid).setValue(user)

    fun getUser(userId: String?) = userId?.let { db.child("users").child(it) }

    fun postQuestion(question: Question) = db.child("questions").push().setValue(question)

    fun getLawyers() = db.child("users").orderByChild("role").equalTo("lawyer")

    fun postCase(case: Case) = db.child("cases").push().setValue(case)

    fun getLawyersCases(uid: String) = db.child("cases").orderByChild("user").equalTo(uid)

    fun uploadImage(file: Uri, uid: String): UploadTask {
        val reference = storage.child(uid)
        return reference.putFile(file)
    }

    fun getImageRef(uid: String) = storage.child(uid)

    fun getDefaultImageRef() = storage.child("default.png")

    fun updateCountry(country: String, uid: String) =
        db.child("users").child(uid).child("country").setValue(country)

    fun updateCity(city: String, uid: String) =
        db.child("users").child(uid).child("city").setValue(city)

    fun updatePhone(phone: String, uid: String) =
        db.child("users").child(uid).child("phone").setValue(phone)

    fun updateWonCases(wonCases: Int, uid: String) =
        db.child("users").child(uid).child("wonCases").setValue(wonCases)

    fun updateExperience(experience: Int, uid: String) =
        db.child("users").child(uid).child("experience").setValue(experience)

    fun updateSpecialization(specialization: String, uid: String) =
        db.child("users").child(uid).child("specialization").setValue(specialization)

    fun updateEducation(education: String, uid: String) =
        db.child("users").child(uid).child("education").setValue(education)
}
