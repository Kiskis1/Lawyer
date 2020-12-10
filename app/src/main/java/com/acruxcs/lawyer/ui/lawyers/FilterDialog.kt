package com.acruxcs.lawyer.ui.lawyers

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.DialogFilterBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.utils.Utils.cityAdapter
import com.acruxcs.lawyer.utils.Utils.countryAdapter
import com.acruxcs.lawyer.utils.Utils.experienceAdapter
import com.acruxcs.lawyer.utils.Utils.specializationAdapter
import com.acruxcs.lawyer.utils.Utils.yes
import java.util.function.Predicate

class FilterDialog(private val fragment: Fragment) : DialogFragment() {

    private lateinit var listener: OnFilterButtonClickListener

    private lateinit var binding: DialogFilterBinding

    private val countryPredicate by lazy {
        Predicate<User> { l: User ->
            l.country == binding.filterSpinnerCountry.editableText.toString().trim()
        }
    }

    private val cityPredicate by lazy {
        Predicate<User> { l: User ->
            l.city == binding.filterSpinnerCity.editableText.toString().trim()
        }
    }

    private val specPredicate by lazy {
        Predicate<User> { l: User ->
            l.specialization == binding.filterSpinnerSpecialization.editableText.toString().trim()
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

                filterSpinnerCountry.setAdapter(countryAdapter)
                filterSpinnerCity.setAdapter(cityAdapter)

                filterSpinnerSpecialization.setAdapter(specializationAdapter)
                filterSpinnerExperience.setAdapter(experienceAdapter)

                builder.setView(binding.root)
                    .setPositiveButton(
                        R.string.action_filter
                    ) { dialog, _ ->
                        filterSpinnerCountry.text.toString().isNotEmpty()
                            .yes { allPredicates.add(countryPredicate) }
                        filterSpinnerCity.text.toString().isNotEmpty()
                            .yes { allPredicates.add(cityPredicate) }
                        filterSpinnerSpecialization.editableText.toString().isNotEmpty()
                            .yes { allPredicates.add(specPredicate) }
                        filterSpinnerExperience.editableText.toString().isNotEmpty()
                            .yes { allPredicates.add(expPredicate) }
                        listener.onFilterButtonClick(allPredicates)
                        dialog.cancel()
                    }
                    .setNegativeButton(
                        R.string.action_cancel
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
