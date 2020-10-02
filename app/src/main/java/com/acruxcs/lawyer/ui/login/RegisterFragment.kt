package com.acruxcs.lawyer.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val TAG = this::class.java.simpleName

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        register_button_register.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val email = edit_email.text.toString().trim()
        val password = edit_password.text.toString().trim()
        val nickname = edit_nickname.text.toString().trim()
        val country = edit_country.text.toString().trim()
        val city = edit_city.text.toString().trim()
        if (email.isEmpty()) {
            register_layout_edit_email.error = getString(R.string.empty_field)
            edit_email.requestFocus()
            return
        }
        if (password.isEmpty()) {
            register_layout_edit_password.error = getString(R.string.empty_field)
            edit_password.requestFocus()
            return
        }
        if (password.length < 6) {
            register_layout_edit_password.error = getString(R.string.empty_field)
            edit_password.requestFocus()
            return
        }
        if (nickname.isEmpty()) {
            register_layout_edit_nickname.error = getString(R.string.empty_field)
            edit_nickname.requestFocus()
            return
        }
        if (country.isEmpty()) {
            register_layout_edit_country.error = getString(R.string.empty_field)
            edit_country.requestFocus()
            return
        }
        if (city.isEmpty()) {
            register_layout_edit_city.error = getString(R.string.empty_field)
            edit_city.requestFocus()
            return
        }
        val user = User(
            email, password, nickname, country, city
        )

        createAccount(user)
    }

    private fun createAccount(user: User) {
        viewModel.firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    viewModel.createNewUser(user)
                    requireView().findNavController()
                        .navigate(R.id.action_registerFragment_to_mainFragment)
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