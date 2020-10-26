package com.acruxcs.lawyer.ui.profile

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import coil.request.CachePolicy
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Case
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.ui.lawyersinfo.LawyersCaseAdapter
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.ui.main.MainViewModel.Companion.Status
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.acruxcs.lawyer.utils.Utils.edit
import com.facebook.login.LoginManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.StorageReference
import io.github.rosariopfernandes.firecoil.load
import kotlinx.android.synthetic.main.fragment_profile_lawyer.*
import kotlinx.android.synthetic.main.fragment_profile_user.profile_button_edit_picture
import kotlinx.android.synthetic.main.fragment_profile_user.profile_button_logout
import kotlinx.android.synthetic.main.fragment_profile_user.profile_edit_city
import kotlinx.android.synthetic.main.fragment_profile_user.profile_edit_country
import kotlinx.android.synthetic.main.fragment_profile_user.profile_edit_password
import kotlinx.android.synthetic.main.fragment_profile_user.profile_edit_phone
import kotlinx.android.synthetic.main.fragment_profile_user.profile_image_picture
import kotlinx.android.synthetic.main.fragment_profile_user.profile_layout_city
import kotlinx.android.synthetic.main.fragment_profile_user.profile_layout_country
import kotlinx.android.synthetic.main.fragment_profile_user.profile_layout_password
import kotlinx.android.synthetic.main.fragment_profile_user.profile_layout_phone
import kotlinx.android.synthetic.main.fragment_profile_user.profile_switch_dark_mode

class ProfileFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val lawyersCasesAdapter = LawyersCaseAdapter()
    private val list = mutableListOf<Case>()
    private val lawyersViewModel: LawyersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return if (viewModel.user.value!!.role == "lawyer")
            inflater.inflate(R.layout.fragment_profile_lawyer, container, false)
        else inflater.inflate(R.layout.fragment_profile_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatus().observe(this, { handleStatus(it) })
        profile_button_edit_picture.setOnClickListener {
            selectImage()
        }
        profile_layout_password.setEndIconOnClickListener {
            updatePassword()
        }

        profile_button_logout.setOnClickListener {
            logout()
        }

        profile_switch_dark_mode.isChecked =
            Utils.preferences.getBoolean(Utils.SHARED_DARK_MODE_ON, false)
        profile_switch_dark_mode.setOnCheckedChangeListener { _, b ->
            Utils.preferences.edit {
                it.putBoolean(Utils.SHARED_DARK_MODE_ON, b)
            }
            Utils.switchDarkMode(b)
        }
        loadProfileImage()

        profile_edit_country.setText(viewModel.user.value!!.country)
        profile_edit_city.setText(viewModel.user.value!!.city)
        profile_edit_phone.setText(viewModel.user.value!!.phone)

        profile_layout_country.setEndIconOnClickListener {
            updateCountry()
        }

        profile_layout_city.setEndIconOnClickListener {
            updateCity()
        }

        profile_layout_phone.setEndIconOnClickListener {
            updatePhone()
        }

        //lawyers profile views
        profile_fab_add_case?.setOnClickListener {
            NewCaseDialog(this).show(parentFragmentManager, "new_case")
        }

        profile_recycler?.adapter = lawyersCasesAdapter
        lawyersViewModel.getLawyersCases().observe(viewLifecycleOwner, {
            list.clear()
            list.addAll(it)
            lawyersCasesAdapter.swapData(list)
        })
    }

    private fun updatePassword() {
        val password = profile_edit_password.text.toString().trim()
        if (password.isEmpty()) {
            profile_layout_password.error = getString(R.string.empty_field)
            profile_edit_password.requestFocus()
            return
        } else if (password.length < MIN_PASS_LENGTH) {
            profile_layout_password.error = getString(R.string.password_not_long_enough)
            profile_edit_password.requestFocus()
            return
        }
        viewModel.updatePassword(password)
        Utils.hideKeyboard(requireContext(), requireView())
    }

    private fun updateCountry() {
        val country = profile_edit_country.text.toString().trim()
        if (country.isEmpty()) {
            profile_layout_country.error = getString(R.string.empty_field)
            profile_layout_country.requestFocus()
            return
        }
        Utils.hideKeyboard(requireContext(), requireView())
        viewModel.updateCountry(country)
    }

    private fun updateCity() {
        val city = profile_edit_city.text.toString().trim()
        if (city.isEmpty()) {
            profile_layout_city.error = getString(R.string.empty_field)
            profile_layout_city.requestFocus()
            return
        }
        Utils.hideKeyboard(requireContext(), requireView())
        viewModel.updateCity(city)
    }

    private fun updatePhone() {
        val phone = profile_edit_phone.text.toString().trim()
        if (phone.isEmpty()) {
            profile_layout_phone.error = getString(R.string.empty_field)
            profile_layout_phone.requestFocus()
            return
        }
        Utils.hideKeyboard(requireContext(), requireView())
        viewModel.updatePhone(phone)
    }

    private fun loadProfileImage() {
        viewModel.getImageRef(
            viewModel.user.value!!.uid,
            object : MainViewModel.Companion.ImageCallback {
                override fun onCallback(value: StorageReference) {
                    profile_image_picture.load(value) {
                        memoryCachePolicy(CachePolicy.DISABLED)
                    }
                }
            })
    }

    private fun logout() {
        viewModel.firebaseAuth.signOut()
        LoginManager.getInstance()?.logOut()
        (activity as MainActivity).googleSignInClient.signOut()
        requireView().findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
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
            profile_image_picture.invalidate()
            uploadImage(filePath)
        }
    }

    private fun uploadImage(filePath: Uri) {
        viewModel.uploadImage(filePath)
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
