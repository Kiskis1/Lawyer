package com.acruxcs.lawyer.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.dialog_new_case.*
import kotlinx.android.synthetic.main.dialog_new_case.view.*

class NewCaseDialog(private val fragment: Fragment) : DialogFragment() {
    private val case = Case()
    private lateinit var thisView: View
    private val viewModel: MainViewModel by activityViewModels()
    private val picker = MaterialDatePicker.Builder.datePicker().build()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return thisView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            thisView = inflater.inflate(R.layout.dialog_new_case, null)
            thisView.case_edit_date.inputType = InputType.TYPE_NULL
            thisView.case_button_add.setOnClickListener {
                if (isValid()) {
                    case.shortDesc = case_edit_description.text.toString().trim()
                    case.court = case_edit_court.text.toString().trim()
                    case.area = case_edit_area.text.toString().trim()
                    case.type = case_edit_type.text.toString().trim()
                    case.outcome = case_edit_outcome.text.toString().trim()
                    viewModel.postCase(case)
                    Utils.hideKeyboard(requireContext(), thisView)
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
            thisView.case_edit_date.setOnClickListener {
                picker.show(fragment.parentFragmentManager, "datepicker")
                picker.addOnPositiveButtonClickListener {
                    case.date = it
                    case_edit_date.setText(picker.headerText)
                }
            }

            builder.setView(thisView)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun isValid(): Boolean {
        var valid = true
        if (TextUtils.isEmpty(case_edit_description.text)) {
            case_edit_description.error = getString(R.string.empty_field)
            valid = false
        } else case_layout_description.error = null

        if (TextUtils.isEmpty(case_edit_court.text)) {
            case_edit_court.error = getString(R.string.empty_field)
            valid = false
        } else case_layout_court.error = null

        if (TextUtils.isEmpty(case_edit_area.text)) {
            case_edit_area.error = getString(R.string.empty_field)
            valid = false
        } else case_layout_area.error = null

        if (TextUtils.isEmpty(case_edit_type.text)) {
            case_edit_type.error = getString(R.string.empty_field)
            valid = false
        } else case_layout_type.error = null

        if (TextUtils.isEmpty(case_edit_outcome.text)) {
            case_edit_outcome.error = getString(R.string.empty_field)
            valid = false
        } else case_layout_outcome.error = null

        if (TextUtils.isEmpty(case_edit_date.text)) {
            case_edit_date.error = getString(R.string.empty_field)
            valid = false
        } else case_layout_date.error = null
        return valid
    }
}
