package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentQuestionBinding
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.QuestionsRepository
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.viewbinding.viewBinding

class QuestionFragment : Fragment(R.layout.fragment_question) {
    private val repository = QuestionsRepository

    private var question: Question? = null
    private var lawyer: User? = null
    private val binding by viewBinding(FragmentQuestionBinding::bind)
    private var tagas: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(ARG_LAWYER)
            tagas = it.getString("tag")
            question = it.getParcelable("question")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditTexts()
        with(binding) {
            if (tagas != null && tagas == "edit_question") {
                toolbar.toolbar.setTitle(R.string.dialog_title_edit)
                editDescription.setText(question?.description)
                editCountry.setText(question?.country)
                editCity.setText(question?.city)
                editPhone.setText(question?.phone)
                editName.setText(question?.fullname)
            } else {
                question = Question()
                toolbar.toolbar.setTitle(R.string.dialog_title_new_case)
            }
            toolbar.toolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_confirm -> {
                            if (isValid()) {
                                question!!.description = editDescription.text.toString().trim()
                                question!!.country = editCountry.text.toString().trim()
                                question!!.city = editCity.text.toString().trim()
                                question!!.phone = editPhone.text.toString().trim()
                                question!!.fullname = editName.text.toString().trim()
                                if (tagas == null) question!!.destination = lawyer!!.uid
                                question!!.sender = MainApplication.user.value!!.uid
                                repository.postQuestion(question!!)
                                Utils.hideKeyboard(requireContext(), binding.root)
                                shortToast(R.string.success)
                                findNavController().navigateUp()
                            }
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    private fun setupEditTexts() {
        with(binding) {
            editCountry.setText(MainApplication.user.value!!.country)
            editCity.setText(MainApplication.user.value!!.city)
            editPhone.setText(MainApplication.user.value!!.phone)
            editName.setText(MainApplication.user.value!!.fullname)
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(
                editDescription, layoutDescription, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editCountry, layoutCountry, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editCity, layoutCity, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editPhone, layoutPhone, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editName, layoutName, requireContext()
            ).yes { valid = false }
        }
        return valid
    }
}
