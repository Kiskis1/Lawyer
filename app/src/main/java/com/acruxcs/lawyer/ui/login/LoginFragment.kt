package com.acruxcs.lawyer.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.ActivityViewModel
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentLoginBinding
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_AUTH_PROVIDER
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_LOGGED_IN
import com.acruxcs.lawyer.repository.SharedPrefRepository.preferences
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
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

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var callbackManager: CallbackManager
    private val viewModel: LoginViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()
    private val binding by viewBinding(FragmentLoginBinding::bind)

    private lateinit var activityProgressLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProgressLayout = (activity as MainActivity).binding.progressBar.progressLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonLogin.isEnabled = true

        //facebook login
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, facebookCallBack)

        with(binding) {
            //google sign in
            buttonLoginGoogle.setOnClickListener {
                errorMessage.text = null
                activityProgressLayout.visibility = View.VISIBLE
                val signInIntent = (activity as MainActivity).googleSignInClient.signInIntent
                googleLoginForResult.launch(signInIntent)
            }

            //facebook sign in
            buttonLoginFacebook.setOnClickListener {
                activityProgressLayout.visibility = View.VISIBLE
                errorMessage.text = null
                LoginManager.getInstance()
                    .logInWithReadPermissions(this@LoginFragment, listOf("email", "public_profile"))
            }

            //normal login
            buttonLogin.setOnClickListener {
                Utils.hideKeyboard(requireContext(), requireView())
                login()
            }

            textRegisterNow.setOnClickListener {
                view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }

            editPassword.setOnEditorActionListener { _, i, _ ->
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
            buttonLogin.isEnabled = false
            errorMessage.text = null
            layoutEmail.error = null
            layoutPassword.error = null
            email = editEmail.text.toString().trim()
            password = editPassword.text.toString().trim()
            checkFieldIfEmpty(editEmail, layoutEmail, requireContext())
                .yes {
                    buttonLogin.isEnabled = true
                    return@login
                }
            checkFieldIfEmpty(editPassword, layoutPassword, requireContext())
                .yes {
                    buttonLogin.isEnabled = true
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
                activityViewModel.getUserData(user.uid)
                Utils.hideKeyboard(requireContext(), requireView())
                preferences.edit {
                    this.putStringSet(SHARED_AUTH_PROVIDER, getProviderIdSet(user))
                    this.putBoolean(SHARED_LOGGED_IN, true)
                }
                requireView().findNavController()
                    .navigate(R.id.action_loginFragment_to_mainFragment)
            } else {
                binding.errorMessage.text = task.exception?.message
                binding.buttonLogin.isEnabled = true
                activityProgressLayout.visibility = View.GONE
            }
        }
    }

    private val googleLoginForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                try {
                    val result = account.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(result!!.idToken!!, null)
                    viewModel.firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            val user = task.result!!.user!!
                            if (task.result!!.additionalUserInfo!!.isNewUser) {
                                viewModel.createNewUser(task)
                            } else {
                                activityViewModel.getUserData(user.uid)
                            }
                            preferences.edit {
                                this.putStringSet(SHARED_AUTH_PROVIDER, getProviderIdSet(user))
                                this.putBoolean(SHARED_LOGGED_IN, true)
                            }

                            activityProgressLayout.visibility = View.GONE
                            requireView().findNavController()
                                .navigate(R.id.action_loginFragment_to_mainFragment)

                        }
                } catch (e: ApiException) {
                    activityProgressLayout.visibility = View.GONE
                    binding.errorMessage.text = e.message
                }
            }
        }

    private val facebookCallBack = object : FacebookCallback<LoginResult> {
        override fun onSuccess(result: LoginResult) {
            val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
            viewModel.firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = task.result!!.user!!
                        if (task.result!!.additionalUserInfo!!.isNewUser) {
                            viewModel.createNewUser(task)
                        } else {
                            activityViewModel.getUserData(user.uid)
                        }
                        preferences.edit {
                            this.putStringSet(SHARED_AUTH_PROVIDER, getProviderIdSet(user))
                        }
                        activityProgressLayout.visibility = View.GONE
                        preferences.edit {
                            this.putBoolean(SHARED_LOGGED_IN, true)
                        }
                        requireView().findNavController()
                            .navigate(R.id.action_loginFragment_to_mainFragment)
                    } else {
                        binding.errorMessage.text = task.exception?.message
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun getProviderIdSet(user: FirebaseUser): MutableSet<String> {
        val set = mutableSetOf<String>()
        for (providerData in user.providerData) {
            set.add(providerData.providerId)
        }
        return set
    }
}
