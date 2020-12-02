package com.acruxcs.lawyer.ui.login

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentRegisterBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.UserTypes
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.checkSpinnerIfEmpty
import com.acruxcs.lawyer.utils.Utils.getCitiesByCountry
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var fullname: String
    private lateinit var country: String
    private lateinit var city: String
    private lateinit var phone: String

    private lateinit var user: User

    private val binding by viewBinding(FragmentRegisterBinding::bind)

    private lateinit var activityProgressLayout: FrameLayout

    private lateinit var selectedCountry: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProgressLayout = (activity as MainActivity).binding.progressBar.progressLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            ArrayAdapter.createFromResource(
                requireContext(),
                R.array.Countries,
                android.R.layout.simple_dropdown_item_1line
            ).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCountry.setAdapter(it)
            }

            spinnerCountry.setOnItemClickListener { adapterView, _, i, _ ->
                spinnerCity.text.clear()
                selectedCountry = adapterView.getItemAtPosition(i).toString()
                ArrayAdapter.createFromResource(
                    requireContext(),
                    getCitiesByCountry(selectedCountry),
                    android.R.layout.simple_dropdown_item_1line
                ).also {
                    it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCity.setAdapter(it)
                }

                Utils.hideKeyboard(requireContext(), requireView())
            }

            spinnerCity.setOnItemClickListener { _, _, _, _ ->
                Utils.hideKeyboard(requireContext(), requireView())
            }

            buttonRegister.setOnClickListener {
                register()
            }
        }
    }

    private fun register() {
        with(binding) {
            email = editEmail.text.toString().trim()
            password = editPassword.text.toString().trim()
            fullname = editFullName.text.toString().trim()
            phone = editPhone.text.toString().trim()
            country = spinnerCountry.editableText.toString()
            city = spinnerCity.editableText.toString()
            if (isValid()) {
                user = User(
                    email, fullname, country, city, phone
                )
                if (!checkboxLawyer.isChecked) {
                    user.role = UserTypes.User
                } else {
                    user.role = UserTypes.Lawyer
                }

                Utils.hideKeyboard(requireContext(), requireView())
                activityProgressLayout.visibility = View.VISIBLE
                createAccount(user)
            }
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(
                editEmail, layoutEmail, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editPassword, layoutPassword, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editFullName, layoutFullName, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editPhone, layoutPhone, requireContext()
            ).yes { valid = false }
            if (password.length < MIN_PASS_LENGTH) {
                layoutPassword.error =
                    getString(R.string.error_password_not_long_enough)
                editPassword.requestFocus()
                valid = false
            } else layoutPassword.error = null

            checkSpinnerIfEmpty(
                spinnerCountry, layoutCountry, requireContext()
            ).yes {
                valid = false
            }
            checkSpinnerIfEmpty(spinnerCity, layoutCity, requireContext()).yes {
                valid = false
            }

            if (resources.getStringArray(getCitiesByCountry(selectedCountry)).contains(city)) {
                layoutCity.error = getString(R.string.error_invalid_city)
                spinnerCity.requestFocus()
                valid = false
            } else layoutCity.error = null
        }
        return valid
    }

    private fun createAccount(user: User) {
        viewModel.firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    user.uid = task.result?.user!!.uid
                    viewModel.createNewUser(user)
                    MainApplication.user.postValue(user)
                    Utils.preferences.edit {
                        this.putBoolean(Utils.SHARED_LOGGED_IN, true)
                    }
                    activityProgressLayout.visibility = View.GONE
                    requireView().findNavController()
                        .navigate(
                            R.id.action_registerFragment_to_mainFragment
                        )
                } else {
                    // If sign in fails, display a message to the user.
                    activityProgressLayout.visibility = View.GONE
                    Toast.makeText(
                        requireActivity(), task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
