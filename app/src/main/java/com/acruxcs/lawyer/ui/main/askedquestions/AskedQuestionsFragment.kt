package com.acruxcs.lawyer.ui.main.askedquestions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentAskedQuestionsBinding
import com.crazylegend.viewbinding.viewBinding

class AskedQuestionsFragment : Fragment(R.layout.fragment_asked_questions) {
    private val viewModel: QuestionsViewModel by viewModels()
    private val binding by viewBinding(FragmentAskedQuestionsBinding::bind)
    private val lawyersAdapter by lazy { QuestionListAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            progressBar.progressLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.INVISIBLE
            recyclerView.adapter = lawyersAdapter

            viewModel.getAskedQuestions(MainApplication.user.value!!.email)
                .observe(viewLifecycleOwner, {
                    lawyersAdapter.swapData(it)
                    progressBar.progressLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                })

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AskedQuestionsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
