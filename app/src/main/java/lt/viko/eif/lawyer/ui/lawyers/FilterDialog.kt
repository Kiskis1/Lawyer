package lt.viko.eif.lawyer.ui.lawyers

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewAnimationUtils
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.DialogFilterBinding
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.utils.Utils
import lt.viko.eif.lawyer.utils.Utils.getAllCityAdapter
import lt.viko.eif.lawyer.utils.Utils.getExperienceAdapter
import lt.viko.eif.lawyer.utils.Utils.getSpecializationAdapter
import lt.viko.eif.lawyer.utils.Utils.yes
import java.util.function.Predicate
import kotlin.math.hypot

class FilterDialog(private val fragment: Fragment) : DialogFragment() {

    private lateinit var listener: OnFilterButtonClickListener

    private lateinit var binding: DialogFilterBinding

    // private val countryPredicate by lazy {
    //     Predicate<User> { l: User ->
    //         l.country == binding.filterSpinnerCountry.editableText.toString().trim()
    //     }
    // }

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
            l.experience >= binding.filterSpinnerExperience.editableText.toString()
        }
    }
    private val allPredicates = mutableListOf<Predicate<User>>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogFilterBinding.inflate(LayoutInflater.from(requireContext()))
        return activity?.let { activity ->
            val builder = MaterialAlertDialogBuilder(activity)
            with(binding) {

                // filterSpinnerCountry.setAdapter(getCountryAdapter(fragment.requireContext()))
                filterSpinnerCity.setAdapter(getAllCityAdapter(fragment.requireContext()))

                filterSpinnerSpecialization.setAdapter(getSpecializationAdapter(fragment.requireContext()))
                filterSpinnerExperience.setAdapter(getExperienceAdapter(fragment.requireContext()))

                builder.setView(binding.root)
                    .setPositiveButton(
                        R.string.action_filter
                    ) { dialog, _ ->
                        // filterSpinnerCountry.text.toString().isNotEmpty()
                        //     .yes { allPredicates.add(countryPredicate) }
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

                val fab = (fragment as LawyersFragment).binding.fab
                val dialog = builder.create()
                val view = dialog.window?.decorView
                filterSpinnerCity.setOnItemClickListener { _, v, _, _ ->
                    Utils.hideKeyboard(fragment.requireContext(), v)
                }
                dialog.setOnShowListener {
                    val endRadius = hypot(view!!.width.toDouble(), view.height.toDouble()).toInt()
                    val cx = (fab.x + fab.width / 2).toInt()
                    val cy = (fab.y + fab.height + 56).toInt()
                    ViewAnimationUtils.createCircularReveal(view,
                        cx,
                        cy,
                        0F,
                        endRadius.toFloat()).also {
                        it.duration = 350
                        it.start()
                    }
                }
                dialog
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
