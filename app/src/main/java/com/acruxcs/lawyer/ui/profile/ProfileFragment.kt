package com.acruxcs.lawyer.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import coil.metadata
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentProfileBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.lawyersinfo.LawyersCaseAdapter
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Status
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.acruxcs.lawyer.utils.Utils.SHARED_AUTH_PROVIDER
import com.acruxcs.lawyer.utils.Utils.edit
import com.acruxcs.lawyer.utils.Utils.preferences
import com.crazylegend.viewbinding.viewBinding
import com.facebook.login.LoginManager
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel: MainViewModel by activityViewModels()
    private val lawyersCasesAdapter = LawyersCaseAdapter()
    private val list = mutableListOf<Case>()
    private val lawyersViewModel: LawyersViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private val binding by viewBinding(FragmentProfileBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.getStatus().observe(this, { handleStatus(it) })
        with(binding) {
            binding.role = viewModel.user.value!!.role
            profileButtonEditPicture.setOnClickListener {
                selectImage()
            }
            if (!preferences.getStringSet(SHARED_AUTH_PROVIDER, setOf<String>())
                    ?.contains("password")!!
            )
                profileLayoutPassword.visibility = View.GONE

            profileLayoutPassword.setEndIconOnClickListener {
                updatePassword()
            }
            profileButtonLogout.setOnClickListener {
                logout()
            }
            profileSwitchDarkMode.isChecked =
                preferences.getBoolean(Utils.SHARED_DARK_MODE_ON, false)
            profileSwitchDarkMode.setOnCheckedChangeListener { _, b ->
                preferences.edit {
                    it.putBoolean(Utils.SHARED_DARK_MODE_ON, b)
                }
                Utils.switchDarkMode(b)
            }
            loadProfileImage()

            profileEditCountry.setText(viewModel.user.value!!.country)
            profileEditCity.setText(viewModel.user.value!!.city)
            profileEditPhone.setText(viewModel.user.value!!.phone)
            profileEditSpecialization.setText(viewModel.user.value!!.specialization)
            profileEditEducation.setText(viewModel.user.value!!.education)
            profileEditExperience.setText(viewModel.user.value!!.experience.toString())
            profileEditWonCases.setText(viewModel.user.value!!.wonCases.toString())

            profileLayoutCountry.setEndIconOnClickListener {
                updateCountry()
            }
            profileLayoutCity.setEndIconOnClickListener {
                updateCity()
            }
            profileLayoutPhone.setEndIconOnClickListener {
                updatePhone()
            }
            //lawyers profile views
            profileLayoutSpecialization.setEndIconOnClickListener {
                updateSpec()
            }
            profileLayoutEducation.setEndIconOnClickListener {
                updateEducation()
            }
            profileLayoutExperience.setEndIconOnClickListener {
                updateExperience()
            }
            profileLayoutWonCases.setEndIconOnClickListener {
                updateWonCases()
            }
            profileFabAddCase.setOnClickListener {
                NewCaseDialog(this@ProfileFragment).show(parentFragmentManager, "new_case")
            }
            profileRecycler.adapter = lawyersCasesAdapter
            lawyersViewModel.getLawyersCases(viewModel.user.value!!.uid)
                .observe(viewLifecycleOwner, {
                    list.clear()
                    list.addAll(it)
                    lawyersCasesAdapter.swapData(list)
                })
        }
    }

    private fun updateWonCases() {
        with(binding) {
            if (profileEditWonCases.text.toString().isEmpty()) {
                profileLayoutWonCases.error = getString(R.string.empty_field)
                profileEditWonCases.requestFocus()
                return
            }
        }
        val wonCases = Integer.parseInt(binding.profileEditWonCases.text.toString().trim())
        Utils.hideKeyboard(requireContext(), requireView())
        profileViewModel.updateWonCases(wonCases)
    }

    private fun updateExperience() {
        with(binding) {
            if (profileEditExperience.text.toString().isEmpty()) {
                profileLayoutExperience.error = getString(R.string.empty_field)
                profileEditExperience.requestFocus()
                return
            }
        }
        val experience = Integer.parseInt(binding.profileEditExperience.text.toString().trim())
        Utils.hideKeyboard(requireContext(), requireView())
        profileViewModel.updateExperience(experience)
    }

    private fun updateEducation() {
        val education: String
        with(binding) {
            education = profileEditEducation.text.toString().trim()
            if (education.isEmpty()) {
                profileLayoutEducation.error = getString(R.string.empty_field)
                profileEditEducation.requestFocus()
                return
            }
        }
        Utils.hideKeyboard(requireContext(), requireView())
        profileViewModel.updateEducation(education)
    }

    private fun updateSpec() {
        val specialization: String
        with(binding) {
            specialization = profileEditSpecialization.text.toString().trim()
            if (specialization.isEmpty()) {
                profileLayoutSpecialization.error = getString(R.string.empty_field)
                profileEditSpecialization.requestFocus()
                return
            }
        }
        Utils.hideKeyboard(requireContext(), requireView())
        profileViewModel.updateSpecialization(specialization)
    }

    private fun updatePassword() {
        val password: String
        with(binding) {
            password = profileEditPassword.text.toString().trim()
            if (password.isEmpty()) {
                profileLayoutPassword.error = getString(R.string.empty_field)
                profileEditPassword.requestFocus()
                return
            } else if (password.length < MIN_PASS_LENGTH) {
                profileLayoutPassword.error = getString(R.string.password_not_long_enough)
                profileEditPassword.requestFocus()
                return
            }
        }
        profileViewModel.updatePassword(password)
        Utils.hideKeyboard(requireContext(), requireView())
    }

    private fun updateCountry() {
        val country: String
        with(binding) {
            country = profileEditCountry.text.toString().trim()
            if (country.isEmpty()) {
                profileLayoutCountry.error = getString(R.string.empty_field)
                profileEditCountry.requestFocus()
                return
            }
        }
        Utils.hideKeyboard(requireContext(), requireView())
        profileViewModel.updateCountry(country)
    }

    private fun updateCity() {
        val city: String
        with(binding) {
            city = profileEditCity.text.toString().trim()
            if (city.isEmpty()) {
                profileLayoutCity.error = getString(R.string.empty_field)
                profileEditCity.requestFocus()
                return
            }
        }
        Utils.hideKeyboard(requireContext(), requireView())
        profileViewModel.updateCity(city)
    }

    private fun updatePhone() {
        val phone: String
        with(binding) {
            phone = profileEditPhone.text.toString().trim()
            if (phone.isEmpty()) {
                profileLayoutPhone.error = getString(R.string.empty_field)
                profileEditPhone.requestFocus()
                return
            }
        }
        Utils.hideKeyboard(requireContext(), requireView())
        profileViewModel.updatePhone(phone)
    }

    private fun loadProfileImage() {
        viewModel.getImageRef(
            viewModel.user.value!!.uid,
            object : MainViewModel.Companion.ImageCallback {
                override fun onCallback(value: String) {
                    with(binding) {
                        profileImagePicture.load(value) {
                            error(R.drawable.ic_person_24)
                            if (profileImagePicture.metadata != null)
                                placeholderMemoryCacheKey(profileImagePicture.metadata!!.memoryCacheKey)
                        }
                    }
                }
            })
    }

    private fun logout() {
        // requireView().findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        viewModel.firebaseAuth.signOut()
        LoginManager.getInstance()?.logOut()
        (activity as MainActivity).googleSignInClient.signOut()
        requireActivity().finish()
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val filePath = data.data!!
            binding.profileImagePicture.invalidate()
            uploadImage(filePath)
        }
    }

    private fun uploadImage(filePath: Uri) {
        profileViewModel.uploadImage(filePath)
    }

    private fun handleStatus(status: Status?) {
        when (status) {
            Status.SUCCESS ->
                Snackbar.make(requireView(), "Success", Snackbar.LENGTH_LONG).show()
            Status.ERROR -> Toast.makeText(
                context,
                "Something went wrong, please try again!",
                Toast.LENGTH_SHORT
            ).show()
            Status.REAUTHENTICATE -> {
                Toast.makeText(
                    context,
                    "Please re-login",
                    Toast.LENGTH_SHORT
                ).show()
                logout()
            }
            Status.PICTURE_CHANGE_SUCCESS -> {
                loadProfileImage()
                Snackbar.make(requireView(), "Success", Snackbar.LENGTH_LONG).show()
            }
            else -> Toast.makeText(
                context,
                "Something went wrong, please try again!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 22
    }
}
