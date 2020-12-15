package com.acruxcs.lawyer.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentProfileEditBinding
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.countryAdapter
import com.acruxcs.lawyer.utils.Utils.getCitiesByCountry
import com.acruxcs.lawyer.utils.Utils.getCityAdapter
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding

class ProfileEditFragment :
    Fragment(R.layout.fragment_profile_edit) {
    private val binding by viewBinding(FragmentProfileEditBinding::bind)

    private lateinit var selectedCountry: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditTexts()
        with(binding) {
            role = MainApplication.user.value!!.role

            toolbar.toolbar.apply {
                setTitle(R.string.dialog_title_profile_edit)
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_confirm -> {
                            updateProfile()
                            Utils.hideKeyboard(requireContext(), requireView())
                            true
                        }
                        else -> false
                    }
                }
            }
            editCountry.apply {
                setAdapter(countryAdapter)
                setOnItemClickListener { adapterView, _, i, _ ->
                    selectedCountry = adapterView.getItemAtPosition(i).toString()
                    editCity.setAdapter(getCityAdapter(selectedCountry))
                    editCity.isEnabled = true
                    Utils.hideKeyboard(requireContext(), requireView())
                }
            }
            if (MainApplication.user.value!!.country == "")
                editCity.isEnabled = false
            else editCity.setAdapter(getCityAdapter(MainApplication.user.value!!.country))
        }
    }

    private fun updateProfile() {
        if (isValid()) {
            val user = MainApplication.user.value!!
            with(binding) {
                user.phone = editPhone.text.toString().trim()
                user.country = editCountry.editableText.toString().trim()
                user.city = editCity.editableText.toString().trim()
                user.address = editAddress.text.toString().trim()
                user.specialization = editSpecialization.text.toString().trim()
                user.education = editEducation.text.toString().trim()
                user.experience = Integer.parseInt(editExperience.text.toString().trim())
                user.wonCases = Integer.parseInt(editWonCases.text.toString().trim())
            }
            findNavController().previousBackStackEntry?.savedStateHandle?.set("user", user)
            findNavController().navigateUp()
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(editWonCases, layoutWonCases, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(
                editExperience, layoutExperience, requireContext()
            ).yes {
                valid = false
            }
            checkFieldIfEmpty(editEducation, layoutEducation, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editAddress, layoutAddress, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(
                editSpecialization, layoutSpecialization, requireContext()
            ).yes {
                valid = false
            }
            checkFieldIfEmpty(editCountry, layoutCountry, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editCity, layoutCity, requireContext()).yes {
                valid = false
            }
            if (!resources.getStringArray(
                    getCitiesByCountry(
                        editCountry.editableText.toString().trim()
                    )
                )
                    .contains(editCity.editableText.toString().trim())
            ) {
                editCity.editableText.clear()
            }

        }
        return valid
    }

    private fun setupEditTexts() {
        with(binding) {
            editCountry.setText(MainApplication.user.value!!.country)
            editCity.setText(MainApplication.user.value!!.city)
            editPhone.setText(MainApplication.user.value!!.phone)
            editAddress.setText(MainApplication.user.value!!.address)
            editSpecialization.setText(MainApplication.user.value!!.specialization)
            editEducation.setText(MainApplication.user.value!!.education)
            editExperience.setText(MainApplication.user.value!!.experience.toString())
            editWonCases.setText(MainApplication.user.value!!.wonCases.toString())
        }
    }
}
