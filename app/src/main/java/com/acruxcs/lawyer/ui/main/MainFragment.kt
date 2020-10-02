package com.acruxcs.lawyer.ui.main

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.ui.QuestionDialog
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {
    private val TAG = this::class.java.simpleName
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAuth = Firebase.auth
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserData(MainApplication.firebaseUser!!.uid)
            .observe(viewLifecycleOwner,
                { t -> text_main_fragment.text = t?.role })

        val navBar: BottomNavigationView? = activity?.findViewById(R.id.bottom_menu)
        if (navBar != null) {
            navBar.visibility = View.VISIBLE
        }

        main_button_call.setOnClickListener {
            val dialog = context?.let { it1 -> AlertDialog.Builder(it1) }
            dialog!!.setMessage(R.string.call_dialog_message)
            dialog.setTitle(R.string.call_dialog_title)
            dialog.setPositiveButton(R.string.call, DialogInterface.OnClickListener { _, _ ->
                val phone = "+37060000000"
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                startActivity(intent)
            })
            dialog.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { d, _ ->
                d.cancel()
            })
            dialog.create().show()
        }

        main_button_question.setOnClickListener {
            QuestionDialog().show(parentFragmentManager, "Question")
        }

        main_button_logout.setOnClickListener {
            firebaseAuth.signOut()
            view.findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
            LoginManager.getInstance()?.logOut()
            (activity as MainActivity).googleSignInClient.signOut()
        }
    }
}