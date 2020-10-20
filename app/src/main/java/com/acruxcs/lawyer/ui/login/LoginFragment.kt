package com.acruxcs.lawyer.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_login.*

private const val RC_SIGN_IN = 1

class LoginFragment : Fragment(R.layout.fragment_login) {
    private val TAG = this::class.java.simpleName

    private lateinit var callbackManager: CallbackManager
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBar: BottomNavigationView? = activity?.findViewById(R.id.bottom_menu)
        if (navBar != null) {
            navBar.visibility = View.GONE
        }
        val user = viewModel.getCurrentUser()
        if (user != null) {
            view.findNavController()
                .navigate(R.id.action_loginFragment_to_mainFragment)
        }

        //facebook login
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    viewModel.firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                viewModel.createNewUser(task)
                                viewModel.setLoggingIn(true)
                                view.findNavController()
                                    .navigate(R.id.action_loginFragment_to_mainFragment)
                            } else {
                                login_error_message.text = task.exception?.message
                            }
                        }
                }

                override fun onCancel() {
                    Log.e("facebook", "onCancel")
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                }
            })

        //google sign in
        login_button_login_google.setOnClickListener {
            login_error_message.text = null
            login_loading.visibility = View.VISIBLE
            val signInIntent = (activity as MainActivity).googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        //facebook sign in
        login_button_facebook.setOnClickListener {
            login_error_message.text = null
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
        }

        //normal login
        login_button_login.setOnClickListener {
            login()
        }

        text_register_now.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        login_edit_password.setOnEditorActionListener { _, i, _ ->
            return@setOnEditorActionListener when (i) {
                EditorInfo.IME_ACTION_DONE -> {
                    login()
                    true
                }
                else -> false
            }
        }
    }

    private fun login() {
        login_error_message.text = null
        login_layout_edit_email.error = null
        login_layout_edit_password.error = null
        val email = login_edit_email.text.toString().trim()
        val password = login_edit_password.text.toString().trim()
        if (email.isEmpty()) {
            login_layout_edit_email.error = getString(R.string.empty_field)
            login_edit_email.requestFocus()
            return
        }
        if (password.isEmpty()) {
            login_layout_edit_password.error = getString(R.string.empty_field)
            login_edit_password.requestFocus()
            return
        }

        viewModel.firebaseAuth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                viewModel.setLoggingIn(true)
                Utils.hideKeyboard(requireContext(), requireView())
                requireView().findNavController()
                    .navigate(R.id.action_loginFragment_to_mainFragment)
            } else {
                login_error_message.text = task.exception?.message
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        //google sign in
        if (requestCode == RC_SIGN_IN) {
            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val result = account.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(result!!.idToken!!, null)
                viewModel.firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(requireActivity()) { task ->
                        viewModel.createNewUser(task)
                        viewModel.setLoggingIn(true)
                        login_loading.visibility = View.GONE
                        requireView().findNavController()
                            .navigate(R.id.action_loginFragment_to_mainFragment)
                    }
            } catch (e: ApiException) {
                login_loading.visibility = View.GONE
                login_error_message.text = e.message
            }
        }
    }
}
