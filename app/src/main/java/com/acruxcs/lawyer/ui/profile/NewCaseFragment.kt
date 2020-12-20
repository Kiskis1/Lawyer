package com.acruxcs.lawyer.ui.profile

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentNewCaseBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat

const val EDIT_TAG: String = "edit_case"

class NewCaseFragment : Fragment(R.layout.fragment_new_case) {
    private var case: Case? = null
    private val picker = MaterialDatePicker.Builder.datePicker().build()
    private val binding by viewBinding(FragmentNewCaseBinding::bind)
    private var tagas: String? = null
    private val args: NewCaseFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        case = args.case
        tagas = args.tag
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            if (tagas != null && tagas == EDIT_TAG) {
                toolbar.toolbar.setTitle(R.string.dialog_title_edit)
                editDescription.setText(case?.shortDesc)
                editCourt.setText(case?.court)
                editArea.setText(case?.area)
                editType.setText(case?.type)
                editOutcome.setText(case?.outcome)
                editDate.setText(DateFormat.getDateInstance().format(case?.date))
            } else {
                case = Case()
                toolbar.toolbar.setTitle(R.string.dialog_title_new_case)
            }

            editDate.apply {
                inputType = InputType.TYPE_NULL
                setOnClickListener {
                    picker.show(requireParentFragment().parentFragmentManager, "date_picker")
                    picker.addOnPositiveButtonClickListener {
                        case!!.date = it
                        editDate.setText(picker.headerText)
                    }
                }
            }

            toolbar.toolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_confirm -> {
                            if (isValid()) {
                                case!!.shortDesc = editDescription.text.toString().trim()
                                case!!.court = editCourt.text.toString().trim()
                                case!!.area = editArea.text.toString().trim()
                                case!!.type = editType.text.toString().trim()
                                case!!.outcome = editOutcome.text.toString().trim()
                                findNavController().previousBackStackEntry?.savedStateHandle?.set("case",
                                    case)
                                Utils.hideKeyboard(requireContext(), binding.root)
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

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(editDescription, layoutDescription, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editCourt, layoutCourt, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editArea, layoutArea, requireContext()).yes { valid = false }
            checkFieldIfEmpty(editType, layoutType, requireContext()).yes { valid = false }
            checkFieldIfEmpty(editOutcome, layoutOutcome, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editDate, layoutDate, requireContext()).yes { valid = false }
        }
        return valid
    }
}
