package com.acruxcs.lawyer.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLoginBinding
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.SHARED_AUTH_PROVIDER
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.preferences
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

private const val RC_SIGN_IN = 1

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var callbackManager: CallbackManager
    private val viewModel: MainViewModel by activityViewModels()
    private val binding by viewBinding(FragmentLoginBinding::bind)

    private lateinit var activityProgressLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProgressLayout = (activity as MainActivity).binding.progressLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.firebaseUser != null) {
            view.findNavController()
                .navigate(R.id.action_loginFragment_to_mainFragment)
        }

        binding.loginButtonLogin.isEnabled = true

        //facebook login
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    viewModel.firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val user = task.result!!.user!!
                                if (task.result!!.additionalUserInfo!!.isNewUser) {
                                    viewModel.createNewUser(task)
                                } else {
                                    viewModel.getUserData(user.uid)
                                }
                                preferences.edit {
                                    this.putStringSet(SHARED_AUTH_PROVIDER, getProviderIdSet(user))
                                }
                                activityProgressLayout.visibility = View.GONE
                                viewModel.setLoggedIn(true)
                                view.findNavController()
                                    .navigate(R.id.action_loginFragment_to_mainFragment)
                            } else {
                                binding.loginErrorMessage.text = task.exception?.message
                                activityProgressLayout.visibility = View.GONE
                            }
                        }
                }

                override fun onCancel() {
                    Log.e("facebook", "onCancel")
                    activityProgressLayout.visibility = View.GONE
                }

                override fun onError(error: FacebookException?) {
                    Toast.makeText(context, error?.message, Toast.LENGTH_SHORT).show()
                    activityProgressLayout.visibility = View.GONE
                }
            })

        //todo: remove deprecated code
        with(binding) {
            //google sign in
            loginButtonLoginGoogle.setOnClickListener {
                loginErrorMessage.text = null
                activityProgressLayout.visibility = View.VISIBLE
                val signInIntent = (activity as MainActivity).googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }

            //facebook sign in
            loginButtonFacebook.setOnClickListener {
                loginErrorMessage.text = null
                LoginManager.getInstance()
                    .logInWithReadPermissions(this@LoginFragment, listOf("email", "public_profile"))
            }

            //normal login
            loginButtonLogin.setOnClickListener {
                Utils.hideKeyboard(requireContext(), requireView())
                login()
            }

            textRegisterNow.setOnClickListener {
                view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            loginEditPassword.setOnEditorActionListener { _, i, _ ->
                return@setOnEditorActionListener when (i) {
                    EditorInfo.IME_ACTION_DONE -> {
                        login()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun login() {
        lateinit var email: String
        lateinit var password: String
        with(binding) {
            loginButtonLogin.isEnabled = false
            loginErrorMessage.text = null
            loginLayoutEditEmail.error = null
            loginLayoutEditPassword.error = null
            email = loginEditEmail.text.toString().trim()
            password = loginEditPassword.text.toString().trim()
            checkFieldIfEmpty(loginEditEmail, loginLayoutEditEmail, requireContext())
                .yes {
                    loginButtonLogin.isEnabled = true
                    return@login
                }
            checkFieldIfEmpty(loginEditPassword, loginLayoutEditPassword, requireContext())
                .yes {
                    loginButtonLogin.isEnabled = true
                    return@login
                }
        }

        activityProgressLayout.visibility = View.VISIBLE
        viewModel.firebaseAuth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val user = task.result!!.user!!
                viewModel.getUserData(user.uid)
                Utils.hideKeyboard(requireContext(), requireView())
                preferences.edit {
                    this.putStringSet(SHARED_AUTH_PROVIDER, getProviderIdSet(user))
                }
                requireView().findNavController()
                    .navigate(R.id.action_loginFragment_to_mainFragment)
            } else {
                binding.loginErrorMessage.text = task.exception?.message
                binding.loginButtonLogin.isEnabled = true
                activityProgressLayout.visibility = View.GONE
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
                        val user = task.result!!.user!!
                        if (task.result!!.additionalUserInfo!!.isNewUser) {
                            viewModel.createNewUser(task)
                        } else {
                            viewModel.getUserData(user.uid)
                        }
                        preferences.edit {
                            this.putStringSet(SHARED_AUTH_PROVIDER, getProviderIdSet(user))
                        }

                        activityProgressLayout.visibility = View.GONE
                        viewModel.setLoggedIn(true)
                        requireView().findNavController()
                            .navigate(R.id.action_loginFragment_to_mainFragment)

                    }
            } catch (e: ApiException) {
                activityProgressLayout.visibility = View.GONE
                binding.loginErrorMessage.text = e.message
            }
        }
    }

    private fun getProviderIdSet(user: FirebaseUser): MutableSet<String> {
        val set = mutableSetOf<String>()
        for (providerData in user.providerData) {
            set.add(providerData.providerId)
        }
        return set
    }
}
