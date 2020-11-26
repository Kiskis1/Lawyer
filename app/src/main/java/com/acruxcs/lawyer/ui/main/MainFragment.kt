package com.acruxcs.lawyer.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentMainBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.utils.Utils
import com.crazylegend.viewbinding.viewBinding
import com.google.gson.Gson

class MainFragment : Fragment(R.layout.fragment_main), MainActivity.DataLoadedListener {
    private val viewModel: MainViewModel by activityViewModels()

    private lateinit var questionAdapter: QuestionListAdapter

    private val binding by viewBinding(FragmentMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity?)?.setActivityListener(this@MainFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        questionAdapter = QuestionListAdapter()
        with(binding) {
            mainAskedQuestionsRecycler.adapter = questionAdapter
            viewModel.user.observe(viewLifecycleOwner, { user ->
                textMainFragment.text = resources.getString(R.string.main_text_user_info, user.role)
                viewModel.getAskedQuestions(user.email)
                    .observe(viewLifecycleOwner, {
                        questionAdapter.swapData(it)
                    })
            })
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
