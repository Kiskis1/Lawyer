package com.acruxcs.lawyer.ui.login

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentRegisterBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.UserTypes
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.crazylegend.viewbinding.viewBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var fullname: String
    private lateinit var country: String
    private lateinit var city: String
    private lateinit var phone: String

    private lateinit var user: User

    // private lateinit var progressLayout: FrameLayout
    private val binding by viewBinding(FragmentRegisterBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val jsonString = Utils.getJsonFromAssets(requireContext(), "countries.min.json")
        val mapType = object : TypeToken<Map<String, List<String>>>() {}.type
        val countryList =
            Gson().fromJson<Map<String, List<String>>>(jsonString, mapType).toSortedMap()

        val countryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ArrayList(countryList.keys)
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        with(binding) {
            registerSpinnerCountry.setAdapter(countryAdapter)

            registerSpinnerCountry.setOnItemClickListener { adapterView, _, i, _ ->
                registerSpinnerCity.text.clear()
                val selected = adapterView.getItemAtPosition(i).toString()
                val cityList = countryList[selected]!!
                val cityAdapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    ArrayList(cityList)
                ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                registerSpinnerCity.setAdapter(cityAdapter)
                Utils.hideKeyboard(requireContext(), requireView())
            }

            registerSpinnerCity.setOnItemClickListener { _, _, _, _ ->
                Utils.hideKeyboard(requireContext(), requireView())
            }

            registerButtonRegister.setOnClickListener {
                register()
            }
        }
    }

    private fun register() {
        with(binding) {
            email = registerEditEmail.text.toString().trim()
            password = registerEditPassword.text.toString().trim()
            fullname = registerEditFullName.text.toString().trim()
            phone = registerEditPhone.text.toString().trim()
            country = registerSpinnerCountry.editableText.toString()
            city = registerSpinnerCity.editableText.toString()
            if (isValid()) {
                user = User(
                    email, fullname, country, city, phone
                )
                if (!registerCheckboxLawyer.isChecked) {
                    user.role = UserTypes.User
                } else {
                    user.role = UserTypes.Lawyer
                }

                Utils.hideKeyboard(requireContext(), requireView())
                // progressLayout.visibility = View.VISIBLE
                createAccount(user)
            }
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            if (email.isEmpty()) {
                registerLayoutEditEmail.error = getString(R.string.empty_field)
                registerEditEmail.requestFocus()
                valid = false
            }
            if (password.isEmpty()) {
                registerLayoutEditPassword.error = getString(R.string.empty_field)
                registerEditPassword.requestFocus()
                valid = false
            }
            if (password.length < MIN_PASS_LENGTH) {
                registerLayoutEditPassword.error = getString(R.string.empty_field)
                registerEditPassword.requestFocus()
                valid = false
            }
            if (fullname.isEmpty()) {
                registerLayoutEditFullName.error = getString(R.string.empty_field)
                registerEditFullName.requestFocus()
                valid = false
            }
            if (phone.isEmpty()) {
                registerLayoutEditPhone.error = getString(R.string.empty_field)
                registerEditPhone.requestFocus()
                valid = false
            }
            if (country.isEmpty()) {
                registerLayoutEditCountry.error = getString(R.string.empty_field)
                registerSpinnerCountry.requestFocus()
                valid = false
            }
            if (city.isEmpty()) {
                registerLayoutEditCity.error = getString(R.string.empty_field)
                registerSpinnerCity.requestFocus()
                valid = false
            }
        }
        return valid
    }

    private fun createAccount(user: User) {
        viewModel.firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    user.uid = task.result?.user!!.uid
                    viewModel.createNewUser(user)
                    viewModel.setUser(user)
                    // progressLayout.visibility = View.GONE
                    requireView().findNavController()
                        .navigate(
                            R.id.action_registerFragment_to_mainFragment
                        )
                } else {
                    // If sign in fails, display a message to the user.
                    // progressLayout.visibility = View.GONE
                    Toast.makeText(
                        requireActivity(), task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}

//TODO when selecting country remove edit ability, return when choosing again
