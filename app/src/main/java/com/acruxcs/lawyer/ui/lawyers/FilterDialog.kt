package com.acruxcs.lawyer.ui.lawyers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.R
import com.google.android.gms.common.util.ArrayUtils
import kotlinx.android.synthetic.main.dialog_filter.*
import kotlinx.android.synthetic.main.dialog_filter.view.*

class FilterDialog(private val fragment: Fragment) : DialogFragment() {

    private lateinit var listener: OnFilterButtonClickListener
    private lateinit var thisView: View

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
            thisView = inflater.inflate(R.layout.dialog_filter, null)

            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.Specializations,
                android.R.layout.simple_dropdown_item_1line
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                thisView.filter_spinner_specialization.setAdapter(adapter)
            }

            val experience = ArrayUtils.toWrapperArray(resources.getIntArray(R.array.Experience))
            val adapter = ArrayAdapter<Int>(
                requireContext(),
                android.R.layout.simple_spinner_item, experience
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            thisView.filter_spinner_experience.setAdapter(adapter)

            builder.setView(thisView)
                .setPositiveButton(
                    R.string.filter
                ) { dialog, _ ->
                    listener.onFilterButtonClick(
                        mapOf(
                            "city" to filter_edit_city.text.toString().trim(),
                            "spec" to filter_spinner_specialization.editableText.toString(),
                            "exp" to filter_spinner_experience.editableText.toString()
                        )
                    )
                    dialog.cancel()
                }
                .setNegativeButton(
                    R.string.cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = fragment as OnFilterButtonClickListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnFilterButtonClickListener")
        }
    }

    interface OnFilterButtonClickListener {
        fun onFilterButtonClick(filter: Map<String, String>)
    }
}