package com.acruxcs.lawyer.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.utils.MainApplication
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
            // Utils.showCallDialog(requireContext())
        }

        main_button_question.setOnClickListener {
            // Utils.showQuestionDialog(parentFragmentManager, item)
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
}