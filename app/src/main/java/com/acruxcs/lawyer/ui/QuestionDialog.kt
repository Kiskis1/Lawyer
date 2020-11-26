package com.acruxcs.lawyer.ui

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.acruxcs.lawyer.databinding.DialogQuestionBinding
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import com.acruxcs.lawyer.utils.Utils.checkFieldsIfEmpty
import com.acruxcs.lawyer.utils.Utils.no
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class QuestionDialog : DialogFragment() {
    private val repository = FirebaseRepository

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
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            with(binding) {
                questionButtonSend.setOnClickListener {
                    if (isValid()) {
                        question.description = questionEditDescription.text.toString().trim()
                        question.country = questionEditLocationCountry.text.toString().trim()
                        question.city = questionEditLocationCity.text.toString().trim()
                        question.phone = questionEditPhone.text.toString().trim()
                        question.fullname = questionEditName.text.toString().trim()
                        question.destinationEmail = lawyer.email
                        question.sender = Firebase.auth.currentUser!!.email.toString()
                        repository.postQuestion(question)
                        Utils.hideKeyboard(requireContext(), binding.root)
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldsIfEmpty(
                questionEditDescription, questionLayoutDescription, requireContext()
            ).no { valid = false }
            checkFieldsIfEmpty(
                questionEditLocationCountry, questionLayoutLocationCountry, requireContext()
            ).no { valid = false }
            checkFieldsIfEmpty(
                questionEditLocationCity, questionLayoutLocationCity, requireContext()
            ).no { valid = false }
            checkFieldsIfEmpty(
                questionEditPhone, questionLayoutPhone, requireContext()
            ).no { valid = false }
            checkFieldsIfEmpty(
                questionEditName, questionLayoutName, requireContext()
            ).no { valid = false }
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
