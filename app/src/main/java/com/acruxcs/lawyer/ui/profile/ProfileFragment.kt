package com.acruxcs.lawyer.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import coil.load
import coil.metadata
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentProfileBinding
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.lawyersinfo.LawyersCaseAdapter
import com.acruxcs.lawyer.utils.Status
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.acruxcs.lawyer.utils.Utils.SHARED_AUTH_PROVIDER
import com.acruxcs.lawyer.utils.Utils.SHARED_LOGGED_IN
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.edit
import com.acruxcs.lawyer.utils.Utils.getCitiesByCountry
import com.acruxcs.lawyer.utils.Utils.preferences
import com.acruxcs.lawyer.utils.Utils.toggleVisibility
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding
import com.facebook.login.LoginManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val lawyersViewModel: LawyersViewModel by viewModels()
    private val viewModel: ProfileViewModel by viewModels()
    private val lawyersCasesAdapter by lazy { LawyersCaseAdapter(this, viewModel) }

    private val binding by viewBinding(FragmentProfileBinding::bind)

    private lateinit var selectedCountry: String

    private val countryAdapter by lazy {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.Countries,
            android.R.layout.simple_dropdown_item_1line
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun getCityAdapter(country: String) = ArrayAdapter.createFromResource(
        requireContext(),
        getCitiesByCountry(country),
        android.R.layout.simple_dropdown_item_1line
    ).also {
        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatus().observe(this, { handleStatus(it) })
        loadProfileImage()
        setupEditTexts()
        setupEndIconListeners()

        with(binding) {
            role = MainApplication.user.value!!.role
            editCountry.setAdapter(countryAdapter)

            editCountry.setOnItemClickListener { adapterView, _, i, _ ->
                selectedCountry = adapterView.getItemAtPosition(i).toString()
                editCity.setAdapter(getCityAdapter(selectedCountry))
                editCity.isEnabled = true
                Utils.hideKeyboard(requireContext(), requireView())
            }

            if (MainApplication.user.value!!.country == "")
                editCity.isEnabled = false
            else editCity.setAdapter(getCityAdapter(MainApplication.user.value!!.country))

            buttonEditPicture.setOnClickListener {
                selectImage()
            }
            if (!preferences.getStringSet(SHARED_AUTH_PROVIDER, setOf<String>())
                    ?.contains("password")!!
            )
                layoutPassword.visibility = View.GONE

            buttonLogout.setOnClickListener {
                logout()
            }
            switchDarkMode.isChecked =
                preferences.getBoolean(Utils.SHARED_DARK_MODE_ON, false)
            switchDarkMode.setOnCheckedChangeListener { _, b ->
                preferences.edit {
                    it.putBoolean(Utils.SHARED_DARK_MODE_ON, b)
                }
                Utils.switchDarkMode(b)
            }

            recyclerView.adapter = lawyersCasesAdapter
            lawyersViewModel.getLawyersCases(MainApplication.user.value!!.uid)
                .observe(viewLifecycleOwner, {
                    if (it.isNotEmpty()) {
                        if (textEmptyList.isVisible) textEmptyList.toggleVisibility()
                        lawyersCasesAdapter.swapData(it)
                    } else
                        textEmptyList.toggleVisibility()
                })
        }
    }

    private fun setupEditTexts() {
        with(binding) {
            editCountry.setText(MainApplication.user.value!!.country)
            editCity.setText(MainApplication.user.value!!.city)
            editPhone.setText(MainApplication.user.value!!.phone)
            editSpecialization.setText(MainApplication.user.value!!.specialization)
            editEducation.setText(MainApplication.user.value!!.education)
            editExperience.setText(MainApplication.user.value!!.experience.toString())
            editWonCases.setText(MainApplication.user.value!!.wonCases.toString())
        }
    }

    private fun setupEndIconListeners() {
        with(binding) {
            layoutCountry.setEndIconOnClickListener {
                updateCountry()
            }
            layoutCity.setEndIconOnClickListener {
                updateCity()
            }
            layoutPhone.setEndIconOnClickListener {
                updatePhone()
            }
            layoutPassword.setEndIconOnClickListener {
                updatePassword()
            }
            //lawyers profile views
            layoutSpecialization.setEndIconOnClickListener {
                updateSpec()
            }
            layoutEducation.setEndIconOnClickListener {
                updateEducation()
            }
            layoutExperience.setEndIconOnClickListener {
                updateExperience()
            }
            layoutWonCases.setEndIconOnClickListener {
                updateWonCases()
            }
            fabAddCase.setOnClickListener {
                NewCaseDialog(this@ProfileFragment, viewModel).show(
                    parentFragmentManager,
                    "new_case"
                )
            }
        }
    }

    private fun updateWonCases() {
        with(binding) {
            val wonCases = Integer.parseInt(editWonCases.text.toString().trim())
            checkFieldIfEmpty(editWonCases, layoutWonCases, requireContext()).yes {
                return@updateWonCases
            }
            Utils.hideKeyboard(requireContext(), requireView())
            viewModel.updateWonCases(wonCases)
        }
    }

    private fun updateExperience() {
        with(binding) {
            val experience = Integer.parseInt(editExperience.text.toString().trim())
            checkFieldIfEmpty(
                editExperience, layoutExperience, requireContext()
            ).yes {
                return@updateExperience
            }
            Utils.hideKeyboard(requireContext(), requireView())
            viewModel.updateExperience(experience)
        }
    }

    private fun updateEducation() {
        with(binding) {
            val education = editEducation.text.toString().trim()
            checkFieldIfEmpty(editEducation, layoutEducation, requireContext()).yes {
                return@updateEducation
            }
            Utils.hideKeyboard(requireContext(), requireView())
            viewModel.updateEducation(education)
        }
    }

    private fun updateSpec() {
        with(binding) {
            checkFieldIfEmpty(
                editSpecialization, layoutSpecialization, requireContext()
            ).yes {
                return@updateSpec
            }
            val specialization = editSpecialization.text.toString().trim()
            Utils.hideKeyboard(requireContext(), requireView())
            viewModel.updateSpecialization(specialization)
        }
    }

    private fun updatePassword() {
        val password: String
        with(binding) {
            password = editPassword.text.toString().trim()
            if (password.isEmpty()) {
                layoutPassword.error = getString(R.string.error_empty_field)
                editPassword.requestFocus()
                return
            } else if (password.length < MIN_PASS_LENGTH) {
                layoutPassword.error = getString(R.string.error_password_not_long_enough)
                editPassword.requestFocus()
                return
            }
        }
        viewModel.updatePassword(password)
        Utils.hideKeyboard(requireContext(), requireView())
    }

    private fun updateCountry() {
        with(binding) {
            val country = editCountry.editableText.toString().trim()
            checkFieldIfEmpty(editCountry, layoutCountry, requireContext()).yes {
                return@updateCountry
            }
            Utils.hideKeyboard(requireContext(), requireView())
            viewModel.updateCountry(country)

            if (!resources.getStringArray(getCitiesByCountry(country))
                    .contains(editCity.editableText.toString().trim())
            ) {
                viewModel.updateCity("")
                editCity.editableText.clear()
            }
        }
    }

    private fun updateCity() {
        with(binding) {
            val city = editCity.text.toString().trim()
            checkFieldIfEmpty(editCity, layoutCity, requireContext()).yes {
                return@updateCity
            }
            Utils.hideKeyboard(requireContext(), requireView())
            viewModel.updateCity(city)
        }
    }

    private fun updatePhone() {
        with(binding) {
            val phone: String = editPhone.text.toString().trim()
            checkFieldIfEmpty(editPhone, layoutPhone, requireContext()).yes {
                return@updatePhone
            }
            Utils.hideKeyboard(requireContext(), requireView())
            viewModel.updatePhone(phone)
        }
    }

    private fun loadProfileImage() {
        viewModel.getImageRef(
            MainApplication.user.value!!.uid,
            object : ProfileViewModel.Companion.ImageCallback {
                override fun onCallback(value: String) {
                    with(binding) {
                        imagePicture.load(value) {
                            error(R.drawable.ic_person_24)
                            if (imagePicture.metadata != null)
                                placeholderMemoryCacheKey(imagePicture.metadata!!.memoryCacheKey)
                        }
                    }
                }
            })
    }

    private fun logout() {
        preferences.edit {
            it.putBoolean(SHARED_LOGGED_IN, false)
        }
        requireView().findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        Firebase.auth.signOut()
        LoginManager.getInstance()?.logOut()
        (activity as MainActivity).googleSignInClient.signOut()
    }

    private val selectImageForResult =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data
                if (intent != null) {
                    binding.imagePicture.invalidate()
                    viewModel.uploadImage(intent.data!!)
                }
            }
        }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        selectImageForResult.launch(Intent.createChooser(intent, "Select Image from here..."))
    }

    private fun handleStatus(status: Status?) {
        when (status) {
            Status.SUCCESS ->
                Snackbar.make(requireView(), "Success", Snackbar.LENGTH_SHORT).show()

            Status.UPDATE_SUCCESS -> {
                Snackbar.make(requireView(), "Success", Snackbar.LENGTH_SHORT).show()
                lawyersCasesAdapter.notifyDataSetChanged()
            }
            Status.ERROR -> Toast.makeText(
                context, "Something went wrong, please try again!", Toast.LENGTH_SHORT
            ).show()

            Status.REAUTHENTICATE -> {
                Toast.makeText(
                    context, "Please re-login", Toast.LENGTH_SHORT
                ).show()
                logout()
            }

            Status.PICTURE_CHANGE_SUCCESS -> {
                loadProfileImage()
                Snackbar.make(requireView(), "Success", Snackbar.LENGTH_SHORT).show()
            }

            Status.NO_CHANGE -> Toast.makeText(
                context, "No change", Toast.LENGTH_SHORT
            ).show()

            else -> Toast.makeText(
                context,
                "Something went wrong, please try again!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
