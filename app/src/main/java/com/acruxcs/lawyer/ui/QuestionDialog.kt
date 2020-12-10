package com.acruxcs.lawyer.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.DialogQuestionBinding
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.QuestionsRepository
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.yes
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class QuestionDialog : DialogFragment() {
    private val repository = QuestionsRepository

    private var question = Question()
    private lateinit var lawyer: User
    private lateinit var binding: DialogQuestionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(ARG_LAWYER)!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogQuestionBinding.inflate(LayoutInflater.from(requireContext()))
        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.DialogTheme)
            builder.setView(binding.root)
            setupEditTexts()
            with(binding) {
                toolbar.toolbar.inflateMenu(R.menu.menu_send)
                toolbar.toolbar.setNavigationOnClickListener {
                    dismiss()
                }
                toolbar.toolbar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_send -> {
                            if (isValid()) {
                                question.description = editDescription.text.toString().trim()
                                question.country = editCountry.text.toString().trim()
                                question.city = editCity.text.toString().trim()
                                question.phone = editPhone.text.toString().trim()
                                question.fullname = editName.text.toString().trim()
                                question.destinationEmail = lawyer.email
                                question.sender = Firebase.auth.currentUser!!.email.toString()
                                repository.postQuestion(question)
                                Utils.hideKeyboard(requireContext(), binding.root)
                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                                dismiss()
                            }
                            true
                        }
                        else -> false
                    }
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
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

    companion object {
        fun newInstance(lawyer: User) =
            QuestionDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_LAWYER, lawyer)
                }
            }
    }
}
