package com.acruxcs.lawyer.ui.lawyers

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.R
import kotlinx.android.synthetic.main.dialog_filter.*

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

            builder.setView(thisView)
                .setPositiveButton(
                    R.string.filter
                ) { dialog, _ ->
                    listener.onFilterButtonClick(
                        mapOf(
                            "city" to filter_edit_city.text.toString().trim(),
                            "spec" to filter_edit_specialization.text.toString().trim(),
                            "exp" to filter_edit_experience.text.toString().trim()
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