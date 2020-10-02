package com.acruxcs.lawyer.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.acruxcs.lawyer.FirebaseRepository
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Question
import com.google.android.material.textfield.TextInputLayout

class QuestionDialog : DialogFragment() {
    private val TAG = this::class.java.simpleName
    private val repository = FirebaseRepository

    private lateinit var description: EditText
    private lateinit var descriptionLayout: TextInputLayout

    private lateinit var country: EditText
    private lateinit var countryLayout: TextInputLayout

    private lateinit var city: EditText
    private lateinit var cityLayout: TextInputLayout

    private lateinit var phone: EditText
    private lateinit var phoneLayout: TextInputLayout

    private lateinit var name: EditText
    private lateinit var nameLayout: TextInputLayout

    private lateinit var sendButton: Button
    private var question = Question()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view: View = inflater.inflate(R.layout.dialog_question, null)
        sendButton = view.findViewById(R.id.question_button_send)
        description = view.findViewById(R.id.question_edit_description)
        descriptionLayout = view.findViewById(R.id.question_layout_description)

        country = view.findViewById(R.id.question_edit_location_country)
        countryLayout = view.findViewById(R.id.question_layout_location_country)

        city = view.findViewById(R.id.question_edit_location_city)
        cityLayout = view.findViewById(R.id.question_layout_location_city)

        phone = view.findViewById(R.id.question_edit_phone)
        phoneLayout = view.findViewById(R.id.question_layout_phone)

        name = view.findViewById(R.id.question_edit_name)
        nameLayout = view.findViewById(R.id.question_layout_name)

        sendButton.setOnClickListener {
            if (TextUtils.isEmpty(description.text)) {
                descriptionLayout.error = getString(R.string.empty_field)
                return@setOnClickListener
            } else descriptionLayout.error = null

            if (TextUtils.isEmpty(country.text)) {
                countryLayout.error = getString(R.string.empty_field)
                return@setOnClickListener
            } else countryLayout.error = null

            if (TextUtils.isEmpty(city.text)) {
                cityLayout.error = getString(R.string.empty_field)
                return@setOnClickListener
            } else cityLayout.error = null

            if (TextUtils.isEmpty(phone.text)) {
                phoneLayout.error = getString(R.string.empty_field)
                return@setOnClickListener
            } else phoneLayout.error = null

            if (TextUtils.isEmpty(name.text)) {
                nameLayout.error = getString(R.string.empty_field)
                return@setOnClickListener
            } else nameLayout.error = null

            question.description = description.text.toString().trim()
            question.country = country.text.toString().trim()
            question.city = city.text.toString().trim()
            question.phone = phone.text.toString().trim()
            question.name = name.text.toString().trim()

            repository.sendQuestion(question)
            val imm: InputMethodManager =
                requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        builder.setView(view)
        return builder.create()
    }
}