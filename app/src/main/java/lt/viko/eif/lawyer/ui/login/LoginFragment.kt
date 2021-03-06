package lt.viko.eif.lawyer.ui.login

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
import com.crazylegend.viewbinding.viewBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import lt.viko.eif.lawyer.ActivityViewModel
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentLoginBinding
import lt.viko.eif.lawyer.repository.SharedPrefRepository.SHARED_AUTH_PROVIDER
import lt.viko.eif.lawyer.repository.SharedPrefRepository.SHARED_LOGGED_IN
import lt.viko.eif.lawyer.repository.SharedPrefRepository.preferences
import lt.viko.eif.lawyer.utils.Utils
import lt.viko.eif.lawyer.utils.Utils.checkFieldIfEmpty
import lt.viko.eif.lawyer.utils.Utils.getProviderIdSet
import lt.viko.eif.lawyer.utils.Utils.yes

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var callbackManager: CallbackManager
    private val viewModel: LoginViewModel by viewModels()
    private val activityViewModel: ActivityViewModel by activityViewModels()
    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val dirToMain = LoginFragmentDirections.actionLoginFragmentToMainFragment()

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
                activityProgressLayout.visibility = View.VISIBLE
                login()
            }

            textRegisterNow.setOnClickListener {
                val dir = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                view.findNavController().navigate(dir)
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
                    .navigate(dirToMain)
            } else {
                binding.errorMessage.text =
                    resources.getString(R.string.error_invalid_username_password)
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
                                .navigate(dirToMain)

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
                            this.putBoolean(SHARED_LOGGED_IN, true)
                        }
                        activityProgressLayout.visibility = View.GONE
                        requireView().findNavController()
                            .navigate(dirToMain)
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
}
