package com.acruxcs.lawyer.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.ui.QuestionDialog
import com.facebook.login.LoginManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main) {
    private val TAG = this::class.java.simpleName
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserData(MainApplication.firebaseUser!!.uid)
            .observe(viewLifecycleOwner,
                { t -> text_main_fragment.text = t?.role })

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.bottom_menu)
        navBar.visibility = View.VISIBLE

        main_button_call.setOnClickListener {
            callDialog()
        }

        main_button_question.setOnClickListener {
            QuestionDialog().show(parentFragmentManager, "Question")
        }

        main_button_logout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        viewModel.firebaseAuth.signOut()
        LoginManager.getInstance()?.logOut()
        (activity as MainActivity).googleSignInClient.signOut()
        requireView().findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
    }

    private fun callDialog() {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setMessage(R.string.call_dialog_message)
        dialog.setTitle(R.string.call_dialog_title)
        dialog.setPositiveButton(R.string.call) { _, _ ->
            val phone = "+37060000000"
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
            startActivity(intent)
        }
        dialog.setNegativeButton(R.string.cancel) { d, _ ->
            d.cancel()
        }
        dialog.create().show()
    }
}