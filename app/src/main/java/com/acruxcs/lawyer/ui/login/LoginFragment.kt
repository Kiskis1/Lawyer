package com.acruxcs.lawyer.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.navigation.findNavController
import com.acruxcs.lawyer.R
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment : Fragment() {
    private val TAG = this::class.java.simpleName

    private val RC_SIGN_IN = 1
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Log.e("facebook", "onSuccess")
                    //TODO: handle facebook sign in here
                    val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                    firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                view.findNavController()
                                    .navigate(R.id.action_loginFragment_to_mainFragment)
                            } else {
                                Toast.makeText(
                                    requireContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

                override fun onCancel() {
                    Log.e("facebook", "onCancel")
                }

                override fun onError(error: FacebookException?) {
                    text_login_error.text = error?.message
                }
            })

        //google sign in
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.default_web_client_id))
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        button_login_google.setOnClickListener {
            loading.visibility = View.VISIBLE
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
//         facebook sign in
        button_login_facebook.setOnClickListener {
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile"))
        }

        button_login.setOnClickListener {
            firebaseAuth.signInWithEmailAndPassword(
                edit_email.text.toString().trim(),
                edit_password.text.toString().trim()
            )
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = firebaseAuth.currentUser
                        view.findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        text_login_error.text = task.exception?.message
                    }
                }
        }
        button_logout.setOnClickListener {
            signOut()
        }

        text_register_now.setOnClickListener {
            view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        //google sign in
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                loading.visibility = View.GONE
                //TODO: handle google sign in here
                val credential = GoogleAuthProvider.getCredential(account!!.idToken!!, null)
                firebaseAuth.signInWithCredential(credential)
                view?.findNavController()?.navigate(R.id.action_loginFragment_to_mainFragment)
            } catch (e: ApiException) {
                e.printStackTrace()
                loading.visibility = View.GONE
                text_login_error.text = e.message
            }
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
    }

}