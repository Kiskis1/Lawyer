package com.acruxcs.lawyer.ui.profile

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.acruxcs.lawyer.databinding.DialogNewCaseBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldsIfEmpty
import com.acruxcs.lawyer.utils.Utils.no
import com.google.android.material.datepicker.MaterialDatePicker

class NewCaseDialog(private val fragment: Fragment) : DialogFragment() {
    private val case = Case()
    private val viewModel: MainViewModel by activityViewModels()
    private val picker = MaterialDatePicker.Builder.datePicker().build()
    private lateinit var binding: DialogNewCaseBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogNewCaseBinding.inflate(LayoutInflater.from(requireContext()))
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)

            with(binding) {
                caseEditDate.inputType = InputType.TYPE_NULL
                caseButtonAdd.setOnClickListener {
                    if (isValid()) {
                        case.shortDesc = caseEditDescription.text.toString().trim()
                        case.court = caseEditCourt.text.toString().trim()
                        case.area = caseEditArea.text.toString().trim()
                        case.type = caseEditType.text.toString().trim()
                        case.outcome = caseEditOutcome.text.toString().trim()
                        viewModel.postCase(case)
                        Utils.hideKeyboard(requireContext(), binding.root)
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                }
                caseEditDate.setOnClickListener {
                    picker.show(fragment.parentFragmentManager, "date_picker")
                    picker.addOnPositiveButtonClickListener {
                        case.date = it
                        caseEditDate.setText(picker.headerText)
                    }
                }
            }

            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldsIfEmpty(caseEditDescription, caseLayoutDescription, requireContext()).no {
                valid = false
            }
            checkFieldsIfEmpty(caseEditCourt, caseLayoutCourt, requireContext()).no {
                valid = false
            }
            checkFieldsIfEmpty(caseEditArea, caseLayoutArea, requireContext()).no { valid = false }
            checkFieldsIfEmpty(caseEditType, caseLayoutType, requireContext()).no { valid = false }
            checkFieldsIfEmpty(caseEditOutcome, caseLayoutOutcome, requireContext()).no {
                valid = false
            }
            checkFieldsIfEmpty(caseEditDate, caseLayoutDate, requireContext()).no { valid = false }
        }
        return valid
    }
}
