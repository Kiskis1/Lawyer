package com.acruxcs.lawyer.repository

import android.net.Uri
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.WorkingHours
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

object UsersRepository {
    private val db = Firebase.database.reference.child("users")
    private val storage = Firebase.storage.reference.child("images")

    fun setUser(user: User) = db.child(user.uid).setValue(user)

    fun getUser(userId: String?) = userId?.let { db.child(it) }

    fun getLawyers() = db.orderByChild("role").equalTo("Lawyer")

    fun uploadImage(file: Uri, uid: String): UploadTask {
        val reference = storage.child(uid)
        println("IMAGE")
        reference.downloadUrl.addOnSuccessListener {
            setUserImage(uid, it.toString())
        }
        return reference.putFile(file)
    }

    private fun setUserImage(uid: String, ref: String) =
        db.child(uid).child("imageRef").setValue(ref)

    fun updateHours(hours: WorkingHours, uid: String) =
        db.child(uid).child("workingHours").setValue(hours)
}
