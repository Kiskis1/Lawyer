package com.acruxcs.lawyer.ui.main

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
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
    private lateinit var progressLayout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity?)?.setActivityListener(this@MainFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBar = requireActivity().findViewById(R.id.bottom_menu)
        progressLayout = requireActivity().findViewById(R.id.activity_main_loading)
        if (viewModel.loggedIn.value == false) {
            progressLayout.visibility = View.VISIBLE
        }
        questionAdapter = QuestionListAdapter()
        main_asked_questions_recycler.adapter = questionAdapter
        viewModel.user.observe(viewLifecycleOwner, { user ->
            text_main_fragment.text = resources.getString(R.string.main_text_user_info, user.role)
            viewModel.getAskedQuestions(user.email)
                .observe(viewLifecycleOwner, {
                    questionAdapter.swapData(it)
                })
            progressLayout.visibility = View.GONE
            navBar.visibility = View.VISIBLE
        })
    }

    override fun dataLoaded() {
        // viewModel.firebaseAuth.signOut()
        val userJson = Utils.preferences.getString(Utils.SHARED_USER_DATA, null)
        val user = Gson().fromJson(userJson, User::class.java)
        viewModel.setUser(user)

        viewModel.loggedIn.value = true
    }
}
