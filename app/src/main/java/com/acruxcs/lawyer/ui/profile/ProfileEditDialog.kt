package com.acruxcs.lawyer.ui.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import coil.load
import coil.metadata
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.DialogProfileEditBinding
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.countryAdapter
import com.acruxcs.lawyer.utils.Utils.getCitiesByCountry
import com.acruxcs.lawyer.utils.Utils.getCityAdapter
import com.acruxcs.lawyer.utils.Utils.yes
import com.google.android.material.snackbar.Snackbar

class ProfileEditDialog(private val viewModel: ProfileViewModel) :
    DialogFragment() {
    private lateinit var binding: DialogProfileEditBinding

    private lateinit var selectedCountry: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogProfileEditBinding.inflate(LayoutInflater.from(requireContext()))
        setupEditTexts()
        loadProfileImage()
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity, R.style.DialogTheme)

            with(binding) {
                role = MainApplication.user.value!!.role
                editCountry.setAdapter(countryAdapter)

                toolbar.toolbar.setNavigationOnClickListener {
                    dismiss()
                }
                toolbar.toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_confirm -> {
                            updateProfile()
                            dismiss()
                            true
                        }
                        else -> false
                    }
                }

                editCountry.setOnItemClickListener { adapterView, _, i, _ ->
                    selectedCountry = adapterView.getItemAtPosition(i).toString()
                    editCity.setAdapter(getCityAdapter(selectedCountry))
                    editCity.isEnabled = true
                    Utils.hideKeyboard(requireContext(), requireView())
                }

                if (MainApplication.user.value!!.country == "")
                    editCity.isEnabled = false
                else editCity.setAdapter(getCityAdapter(MainApplication.user.value!!.country))

                pictureLayout.buttonEditPicture.setOnClickListener {
                    selectImage()
                }
            }

            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun updateProfile() {
        if (isValid()) {
            val user = MainApplication.user.value!!
            with(binding) {
                user.phone = editPhone.text.toString().trim()
                user.country = editCountry.editableText.toString().trim()
                user.city = editCity.editableText.toString().trim()
                user.specialization = editSpecialization.text.toString().trim()
                user.education = editEducation.text.toString().trim()
                user.experience = Integer.parseInt(editExperience.text.toString().trim())
                user.wonCases = Integer.parseInt(editWonCases.text.toString().trim())
            }
            viewModel.updateUser(user)
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

    private val selectImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val intent = it.data
                if (intent != null) {
                    binding.pictureLayout.imagePicture.invalidate()
                    viewModel.uploadImage(intent.data!!) { result ->
                        if (result == 0) {
                            loadProfileImage()
                            Snackbar.make(binding.root, "Success", Snackbar.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Something went wrong, please try again!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        selectImageForResult.launch(Intent.createChooser(intent, "Select Image from here..."))
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
}
