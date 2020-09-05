package com.acruxcs.lawyer.ui.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.acruxcs.lawyer.FirebaseRepository
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {
    private val TAG = this::class.java.simpleName
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_register.setOnClickListener {
            register()
            view.findNavController().navigate(R.id.action_registerFragment_to_mainFragment)
        }
    }

    private fun register() {
        val user = User(
            edit_email.text.toString().trim(),
            edit_password.text.toString().trim(),
            edit_nickname.text.toString().trim(),
            edit_country.text.toString().trim(),
            edit_city.text.toString().trim()
        )
        createAccount(user)
    }

    private fun createAccount(user: User) {
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val fbUser = firebaseAuth.currentUser
                    if (fbUser != null) {
                        FirebaseRepository.writeNewUser(fbUser.uid, user)
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireActivity(), task.exception?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}