package com.acruxcs.lawyer.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment(R.layout.fragment_main), MainActivity.DataLoadedListener {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var navBar: BottomNavigationView
    private lateinit var questionAdapter: QuestionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity?)?.setActivityListener(this@MainFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBar = requireActivity().findViewById(R.id.bottom_menu)
        if (viewModel.loggedIn.value == false) {
            main_loading.visibility = View.VISIBLE
        }
        questionAdapter = QuestionListAdapter()
        main_asked_questions_recycler.adapter = questionAdapter
        viewModel.user.observe(viewLifecycleOwner, {
            text_main_fragment.text = resources.getString(R.string.main_text_user_info, it.role)
            viewModel.getAskedQuestions(it.email)
                .observe(viewLifecycleOwner, {
                    questionAdapter.swapData(it)
                })
            main_loading.visibility = View.GONE
            navBar.visibility = View.VISIBLE
        })
        if (arguments?.getBoolean("isNewUser") == true) {
            main_loading.visibility = View.GONE
            navBar.visibility = View.VISIBLE
        }
    }

    override fun dataLoaded() {
        // viewModel.firebaseAuth.signOut()
        val userJson = Utils.preferences.getString(Utils.SHARED_USER_DATA, null)
        val user = Gson().fromJson(userJson, User::class.java)
        viewModel.setUser(user)

        viewModel.loggedIn.value = true
    }
}
