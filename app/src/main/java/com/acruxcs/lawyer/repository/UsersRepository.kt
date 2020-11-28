package com.acruxcs.lawyer.repository

import android.net.Uri
import com.acruxcs.lawyer.model.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

object UsersRepository {
    private val db = Firebase.database.reference
    private val storage = Firebase.storage.reference.child("images")
    const val defaultPicture =
        "https://firebasestorage.googleapis.com/v0/b/lawyer-fc8a1.appspot.com" +
            "/o/images%2Fdefault.png?alt=media&token=de3a9f9e-333c-414e-8c59-74fdc51db56f"

    fun writeNewUser(user: User) = db.child("users").child(user.uid).setValue(user)

    fun getUser(userId: String?) = userId?.let { db.child("users").child(it) }

    fun getLawyers() = db.child("users").orderByChild("role").equalTo("Lawyer")

    fun uploadImage(file: Uri, uid: String): UploadTask {
        val reference = storage.child(uid)
        return reference.putFile(file)
    }

    fun getImageRef(uid: String) = storage.child(uid)

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
