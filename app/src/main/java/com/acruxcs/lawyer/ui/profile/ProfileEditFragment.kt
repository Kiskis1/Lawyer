package com.acruxcs.lawyer.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentProfileEditBinding
import com.acruxcs.lawyer.model.UserTypes
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.getCitiesByCountry
import com.acruxcs.lawyer.utils.Utils.getCityAdapter
import com.acruxcs.lawyer.utils.Utils.getCountryAdapter
import com.acruxcs.lawyer.utils.Utils.getPaymentTypeAdapter
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding
import com.yariksoffice.lingver.Lingver

class ProfileEditFragment :
    Fragment(R.layout.fragment_profile_edit) {
    private val binding by viewBinding(FragmentProfileEditBinding::bind)
    private val viewModel: ProfileViewModel by viewModels({ requireParentFragment() })

    private lateinit var selectedCountry: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditTexts()
        with(binding) {
            role = MainActivity.user.value!!.role
            wanted = UserTypes.Lawyer
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
                setAdapter(getCountryAdapter(requireContext()))
                setOnItemClickListener { adapterView, _, i, _ ->
                    selectedCountry = adapterView.getItemAtPosition(i).toString()
                    Lingver.getInstance()
                        .setLocale(context, Utils.convertToLocaleCode(selectedCountry))
                    editCity.setAdapter(getCityAdapter(requireContext(), selectedCountry))
                    editCity.isEnabled = true
                    Utils.hideKeyboard(requireContext(), requireView())
                }
            }
            editPaymentType.setAdapter(getPaymentTypeAdapter(requireContext()))
            if (MainActivity.user.value!!.country == "N/A")
                editCity.isEnabled = false
            else editCity.setAdapter(getCityAdapter(requireContext(),
                MainActivity.user.value!!.country))
        }
    }

    private fun updateProfile() {
        if (isValid()) {
            val user = MainActivity.user.value!!
            with(binding) {
                user.phone = editPhone.text.toString().trim()
                user.country = editCountry.editableText.toString().trim()
                user.city = editCity.editableText.toString().trim()
                user.address = editAddress.text.toString().trim()
                user.specialization = editSpecialization.text.toString().trim()
                user.education = editEducation.text.toString().trim()
                user.experience = Integer.parseInt(editExperience.text.toString().trim())
                user.wonCases = Integer.parseInt(editWonCases.text.toString().trim())
                user.paymentTypes = editPaymentType.editableText.toString().trim()
            }
            viewModel.updateUser(user)
            (activity as MainActivity).recreate()
            findNavController().navigateUp()
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(editCountry, layoutCountry, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editCity, layoutCity, requireContext()).yes {
                valid = false
            }
            checkFieldIfEmpty(editPhone, layoutPhone, requireContext()).yes {
                valid = false
            }
            if (!editCountry.editableText.contains("N/A") && !resources.getStringArray(
                    getCitiesByCountry(editCountry.editableText.toString()
                        .trim()))
                    .contains(editCity.editableText.toString().trim())
            ) {
                valid = false
                editCity.editableText.clear()
                editCity.requestFocus()
                layoutCity.error = resources.getString(R.string.error_invalid_city)
            }
            if (role == UserTypes.Lawyer) {
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
                checkFieldIfEmpty(editPaymentType, layoutPaymentType, requireContext()).yes {
                    valid = false
                }
            }
        }
        return valid
    }

    private fun setupEditTexts() {
        with(binding) {
            editCountry.setText(MainActivity.user.value?.country)
            editCity.setText(MainActivity.user.value?.city)
            editPhone.setText(MainActivity.user.value?.phone)
            editAddress.setText(MainActivity.user.value?.address)
            editSpecialization.setText(MainActivity.user.value?.specialization)
            editEducation.setText(MainActivity.user.value?.education)
            editExperience.setText(MainActivity.user.value?.experience.toString())
            editWonCases.setText(MainActivity.user.value?.wonCases.toString())
            editPaymentType.setText(MainActivity.user.value?.paymentTypes)
        }
    }
}
