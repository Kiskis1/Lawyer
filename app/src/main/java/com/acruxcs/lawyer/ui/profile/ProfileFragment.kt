package com.acruxcs.lawyer.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.load
import coil.metadata
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentProfileBinding
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.WorkingHours
import com.acruxcs.lawyer.repository.SharedPrefRepository
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_DARK_MODE_ON
import com.acruxcs.lawyer.repository.SharedPrefRepository.SHARED_LOGGED_IN
import com.acruxcs.lawyer.repository.SharedPrefRepository.edit
import com.acruxcs.lawyer.repository.SharedPrefRepository.preferences
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.lawyersinfo.LawyersCaseAdapter
import com.acruxcs.lawyer.utils.Status
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.acruxcs.lawyer.utils.Utils.toggleVisibility
import com.crazylegend.kotlinextensions.fragments.shortToast
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatus().observe(this, { handleStatus(it) })
        loadProfileImage()

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<User>("user")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.updateUser(it)
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Case>("case")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.postCase(it)
            }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<WorkingHours>("hours")
            ?.observe(
                viewLifecycleOwner) {
                viewModel.saveHours(it)
            }
        with(binding) {
            role = MainApplication.user.value!!.role

            buttonProfileEdit.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_profileEditFragment)
            }

            if (!preferences.getStringSet(
                    SharedPrefRepository.SHARED_AUTH_PROVIDER,
                    setOf<String>()
                )
                    ?.contains("password")!!
            )
                layoutPassword.visibility = View.GONE

            buttonLogout.setOnClickListener {
                logout()
            }
            switchDarkMode.isChecked =
                preferences.getBoolean(SHARED_DARK_MODE_ON, false)
            switchDarkMode.setOnCheckedChangeListener { _, b ->
                preferences.edit {
                    it.putBoolean(SHARED_DARK_MODE_ON, b)
                }
                Utils.switchDarkMode(b)
            }
            buttonWorkingHours.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_workingHoursFragment)
            }

            pictureLayout.buttonEditPicture.setOnClickListener {
                selectImage()
            }

            fabAddCase.setOnClickListener {
                findNavController()
                    .navigate(R.id.action_profileFragment_to_newCaseFragment)
            }

            layoutPassword.setEndIconOnClickListener {
                updatePassword()
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

    private fun loadProfileImage() {
        viewModel.getImageRef(
            MainApplication.user.value!!.uid,
            object : ProfileViewModel.Companion.ImageCallback {
                override fun onCallback(value: String) {
                    with(binding.pictureLayout) {
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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent = it.data
                if (intent != null) {
                    binding.pictureLayout.imagePicture.invalidate()
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
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()

            Status.UPDATE_SUCCESS -> {
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()
                lawyersCasesAdapter.notifyDataSetChanged()
            }
            Status.ERROR -> shortToast(R.string.error_something)

            Status.REAUTHENTICATE -> {
                shortToast("Please re-login")
                logout()
            }

            Status.PICTURE_CHANGE_SUCCESS -> {
                loadProfileImage()
                Snackbar.make(requireView(), R.string.success, Snackbar.LENGTH_SHORT).show()
            }

            Status.NO_CHANGE -> shortToast("No change")

            else -> shortToast(R.string.error_something)
        }
    }
}
