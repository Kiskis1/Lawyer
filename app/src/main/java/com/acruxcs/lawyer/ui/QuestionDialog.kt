package com.acruxcs.lawyer.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.repository.FirebaseRepository
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.ARG_LAWYER
import kotlinx.android.synthetic.main.dialog_question.*
import kotlinx.android.synthetic.main.dialog_question.view.*

class QuestionDialog : DialogFragment() {
    private val repository = FirebaseRepository

    private var question = Question()
    private lateinit var thisView: View
    private lateinit var lawyer: Lawyer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(ARG_LAWYER)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return thisView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            thisView = inflater.inflate(R.layout.dialog_question, null)

            builder.setView(thisView)
            thisView.question_button_send.setOnClickListener {

                if (inputCheck()) {
                    question.description = question_edit_description.text.toString().trim()
                    question.country = question_edit_location_country.text.toString().trim()
                    question.city = question_edit_location_city.text.toString().trim()
                    question.phone = question_edit_phone.text.toString().trim()
                    question.name = question_edit_name.text.toString().trim()
                    question.destinationEmail = lawyer.email

                    repository.sendQuestion(question)
                    Utils.hideKeyboard(requireContext(), thisView)
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun inputCheck(): Boolean {
        if (TextUtils.isEmpty(question_edit_description.text)) {
            question_layout_description.error = getString(R.string.empty_field)
            return false
        } else question_layout_description.error = null

        if (TextUtils.isEmpty(question_edit_location_country.text)) {
            question_layout_location_country.error = getString(R.string.empty_field)
            return false
        } else question_edit_location_country.error = null

        if (TextUtils.isEmpty(question_edit_location_city.text)) {
            question_layout_location_city.error = getString(R.string.empty_field)
            return false
        } else question_layout_location_city.error = null

        if (TextUtils.isEmpty(question_edit_phone.text)) {
            question_layout_phone.error = getString(R.string.empty_field)
            return false
        } else question_layout_phone.error = null

        if (TextUtils.isEmpty(question_edit_name.text)) {
            question_layout_name.error = getString(R.string.empty_field)
            return false
        } else question_layout_name.error = null
        return true
    }

    companion object {
        fun newInstance(lawyer: Lawyer) =
            QuestionDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_LAWYER, lawyer)

                }
            }
    }
}
