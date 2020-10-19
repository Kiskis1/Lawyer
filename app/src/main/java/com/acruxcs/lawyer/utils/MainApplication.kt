package com.acruxcs.lawyer.utils

import android.app.Application
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainApplication : Application() {
    companion object {
        var firebaseUser: FirebaseUser? = Firebase.auth.currentUser
    }
}