package lt.viko.eif.lawyer.ui.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazylegend.viewbinding.viewBinding
import com.yariksoffice.lingver.Lingver
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentProfileEditBinding
import lt.viko.eif.lawyer.model.UserTypes
import lt.viko.eif.lawyer.utils.Utils
import lt.viko.eif.lawyer.utils.Utils.checkFieldIfEmpty
import lt.viko.eif.lawyer.utils.Utils.getCitiesByCountry
import lt.viko.eif.lawyer.utils.Utils.getCityAdapter
import lt.viko.eif.lawyer.utils.Utils.getCountryAdapter
import lt.viko.eif.lawyer.utils.Utils.getExperienceAdapter
import lt.viko.eif.lawyer.utils.Utils.getPaymentTypeAdapter
import lt.viko.eif.lawyer.utils.Utils.getSpecializationAdapter
import lt.viko.eif.lawyer.utils.Utils.yes

class ProfileEditFragment :
    Fragment(R.layout.fragment_profile_edit) {
    private val binding by viewBinding(FragmentProfileEditBinding::bind)
    private val viewModel: ProfileViewModel by viewModels({ requireParentFragment() })
    private var selected = MainActivity.user.value!!.paymentTypes

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
            editSpecialization.setAdapter(getSpecializationAdapter(requireContext()))
            editExperience.setAdapter(getExperienceAdapter(requireContext()))
            editPaymentType.setAdapter(getPaymentTypeAdapter(requireContext()))
            editPaymentType.setOnItemClickListener { adapterView, view, i, l ->
                selected = i
            }

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
                user.experience = editExperience.editableText.toString().trim()
                user.wonCases = Integer.parseInt(editWonCases.text.toString().trim())
                user.paymentTypes = selected
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
            editExperience.setText(MainActivity.user.value?.experience)
            editWonCases.setText(MainActivity.user.value?.wonCases.toString())
            editPaymentType.setText(resources.getStringArray(R.array.PaymentTypes)[MainActivity.user.value?.paymentTypes!!])
        }
    }
}
