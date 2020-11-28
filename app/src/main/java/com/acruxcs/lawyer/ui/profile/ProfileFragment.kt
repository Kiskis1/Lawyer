package com.acruxcs.lawyer.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import coil.load
import coil.metadata
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentProfileBinding
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.lawyersinfo.LawyersCaseAdapter
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Status
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.acruxcs.lawyer.utils.Utils.SHARED_AUTH_PROVIDER
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.checkSpinnerIfEmpty
import com.acruxcs.lawyer.utils.Utils.countriesMapType
import com.acruxcs.lawyer.utils.Utils.edit
import com.acruxcs.lawyer.utils.Utils.preferences
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding
import com.facebook.login.LoginManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val viewModel: MainViewModel by activityViewModels()
    private val lawyersCasesAdapter = LawyersCaseAdapter()
    private val lawyersViewModel: LawyersViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val user by lazy { viewModel.user.value!! }

    private val binding by viewBinding(FragmentProfileBinding::bind)

    private lateinit var selectedCountry: String

    private val jsonString by lazy {
        Utils.getJsonFromAssets(
            requireContext(), "countries.min.json"
        )
    }

    private val countryList by lazy {
        Gson().fromJson<Map<String, List<String>>>(jsonString, countriesMapType).toSortedMap()
    }
    private val countryAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ArrayList(countryList.keys)
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        profileViewModel.getStatus().observe(this, { handleStatus(it) })
        loadProfileImage()
        setupEditTexts()
        setupEndIconListeners()

        with(binding) {
            role = user.role
            profileEditCountry.setAdapter(countryAdapter)

            profileEditCountry.setOnItemClickListener { adapterView, _, i, _ ->
                selectedCountry = adapterView.getItemAtPosition(i).toString()
                val cityList = countryList[selectedCountry]!!
                val cityAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    ArrayList(cityList)
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                profileEditCity.setAdapter(cityAdapter)
                profileEditCity.isEnabled = true
                Utils.hideKeyboard(requireContext(), requireView())
            }
            if (profileEditCountry.editableText.toString().trim().isNotEmpty()) {
                val cityList = countryList[profileEditCountry.editableText.toString().trim()]!!
                val cityAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    ArrayList(cityList)
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                profileEditCity.setAdapter(cityAdapter)
                Utils.hideKeyboard(requireContext(), requireView())
            } else profileEditCity.isEnabled = false

            profileButtonEditPicture.setOnClickListener {
                selectImage()
            }
            if (!preferences.getStringSet(SHARED_AUTH_PROVIDER, setOf<String>())
                    ?.contains("password")!!
            )
                profileLayoutPassword.visibility = View.GONE

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

            profileRecycler.adapter = lawyersCasesAdapter
            lawyersViewModel.getLawyersCases(user.uid)
                .observe(viewLifecycleOwner, {
                    lawyersCasesAdapter.swapData(it)
                })
        }
    }

    private fun setupEditTexts() {
        with(binding) {
            profileEditCountry.setText(user.country)
            profileEditCity.setText(user.city)
            profileEditPhone.setText(user.phone)
            profileEditSpecialization.setText(user.specialization)
            profileEditEducation.setText(user.education)
            profileEditExperience.setText(user.experience.toString())
            profileEditWonCases.setText(user.wonCases.toString())
        }
    }

    private fun setupEndIconListeners() {
        with(binding) {
            profileLayoutCountry.setEndIconOnClickListener {
                if (profileEditCountry.editableText.toString().trim() != user.country)
                    updateCountry()
                else handleStatus(Status.NO_CHANGE)
            }
            profileLayoutCity.setEndIconOnClickListener {
                if (profileEditCity.editableText.toString().trim() != user.city)
                    updateCity()
                else handleStatus(Status.NO_CHANGE)
            }
            profileLayoutPhone.setEndIconOnClickListener {
                updatePhone()
            }
            profileLayoutPassword.setEndIconOnClickListener {
                updatePassword()
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
        }
    }

    private fun updateWonCases() {
        with(binding) {
            val wonCases = Integer.parseInt(profileEditWonCases.text.toString().trim())
            checkFieldIfEmpty(profileEditWonCases, profileLayoutWonCases, requireContext()).yes {
                return@updateWonCases
            }
            Utils.hideKeyboard(requireContext(), requireView())
            profileViewModel.updateWonCases(wonCases)
        }
    }

    private fun updateExperience() {
        with(binding) {
            val experience = Integer.parseInt(profileEditExperience.text.toString().trim())
            checkFieldIfEmpty(
                profileEditExperience, profileLayoutExperience, requireContext()
            ).yes {
                return@updateExperience
            }
            Utils.hideKeyboard(requireContext(), requireView())
            profileViewModel.updateExperience(experience)
        }
    }

    private fun updateEducation() {
        with(binding) {
            val education = profileEditEducation.text.toString().trim()
            checkFieldIfEmpty(profileEditEducation, profileLayoutEducation, requireContext()).yes {
                return@updateEducation
            }
            Utils.hideKeyboard(requireContext(), requireView())
            profileViewModel.updateEducation(education)
        }
    }

    private fun updateSpec() {
        with(binding) {
            checkFieldIfEmpty(
                profileEditSpecialization, profileLayoutSpecialization, requireContext()
            ).yes {
                return@updateSpec
            }
            val specialization = profileEditSpecialization.text.toString().trim()
            Utils.hideKeyboard(requireContext(), requireView())
            profileViewModel.updateSpecialization(specialization)
        }
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
        with(binding) {
            val country = profileEditCountry.editableText.toString().trim()
            checkSpinnerIfEmpty(profileEditCountry, profileLayoutCountry, requireContext()).yes {
                return@updateCountry
            }
            Utils.hideKeyboard(requireContext(), requireView())
            profileViewModel.updateCountry(country)
            if (country != user.country)
                profileEditCity.editableText.clear()
            profileViewModel.updateCity("")
        }
    }

    private fun updateCity() {
        with(binding) {
            val city = profileEditCity.text.toString().trim()
            checkSpinnerIfEmpty(profileEditCity, profileLayoutCity, requireContext()).yes {
                return@updateCity
            }
            Utils.hideKeyboard(requireContext(), requireView())
            profileViewModel.updateCity(city)
        }
    }

    private fun updatePhone() {
        with(binding) {
            val phone: String = profileEditPhone.text.toString().trim()
            checkFieldIfEmpty(profileEditPhone, profileLayoutPhone, requireContext()).yes {
                return@updatePhone
            }
            Utils.hideKeyboard(requireContext(), requireView())
            profileViewModel.updatePhone(phone)
        }
    }

    private fun loadProfileImage() {
        viewModel.getImageRef(
            user.uid,
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

    private val selectImageForResult =
        registerForActivityResult(StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val intent = it.data
                if (intent != null) {
                    binding.profileImagePicture.invalidate()
                    uploadImage(intent.data!!)
                }
            }
        }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        selectImageForResult.launch(Intent.createChooser(intent, "Select Image from here..."))
    }

    private fun uploadImage(filePath: Uri) {
        profileViewModel.uploadImage(filePath)
    }

    private fun handleStatus(status: Status?) {
        when (status) {
            Status.SUCCESS ->
                Snackbar.make(requireView(), "Success", Snackbar.LENGTH_LONG).show()
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
                Snackbar.make(requireView(), "Success", Snackbar.LENGTH_LONG).show()
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
