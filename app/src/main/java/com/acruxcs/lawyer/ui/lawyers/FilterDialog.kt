package com.acruxcs.lawyer.ui.lawyers

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.DialogFilterBinding
import com.acruxcs.lawyer.model.User
import com.google.android.gms.common.util.ArrayUtils
import java.util.function.Predicate

class FilterDialog(private val fragment: Fragment) : DialogFragment() {

    private lateinit var listener: OnFilterButtonClickListener

    private lateinit var binding: DialogFilterBinding

    private val cityPredicate by lazy {
        Predicate<User> { l: User -> l.city == binding.filterEditCity.text.toString() }
    }

    private val specPredicate by lazy {
        Predicate<User> { l: User ->
            l.specialization == binding.filterSpinnerSpecialization.editableText.toString()
        }
    }

    private val expPredicate by lazy {
        Predicate<User> { l: User ->
            l.experience >= binding.filterSpinnerExperience.editableText.toString().toInt()
        }
    }
    private val allPredicates = mutableListOf<Predicate<User>>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            with(binding) {

                ArrayAdapter.createFromResource(
                    requireContext(),
                    R.array.Specializations,
                    android.R.layout.simple_dropdown_item_1line
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    filterSpinnerSpecialization.setAdapter(adapter)
                }

                val experience =
                    ArrayUtils.toWrapperArray(resources.getIntArray(R.array.Experience))
                val experienceAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_item, experience
                )
                experienceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                filterSpinnerExperience.setAdapter(experienceAdapter)

                builder.setView(binding.root)
                    .setPositiveButton(
                        R.string.filter
                    ) { dialog, _ ->
                        if (filterEditCity.text.toString().isNotEmpty()) {
                            allPredicates.add(cityPredicate)
                        }
                        if (filterSpinnerSpecialization.editableText.toString().isNotEmpty()) {
                            allPredicates.add(specPredicate)
                        }
                        if (filterSpinnerExperience.editableText.toString().isNotEmpty()) {
                            allPredicates.add(expPredicate)
                        }
                        listener.onFilterButtonClick(allPredicates)
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        R.string.cancel
                    ) { dialog, _ ->
                        dialog.cancel()
                    }

                builder.create()
            }

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
        fun onFilterButtonClick(filter: MutableList<Predicate<User>>)
    }
}
