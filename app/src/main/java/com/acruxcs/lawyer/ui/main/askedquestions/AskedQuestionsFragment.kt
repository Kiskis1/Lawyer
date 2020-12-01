package com.acruxcs.lawyer.ui.main.askedquestions

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentAskedQuestionsBinding
import com.crazylegend.viewbinding.viewBinding

class AskedQuestionsFragment : Fragment(R.layout.fragment_asked_questions) {
    private val binding by viewBinding(FragmentAskedQuestionsBinding::bind)

    companion object {
        @JvmStatic
        fun newInstance() =
            AskedQuestionsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
