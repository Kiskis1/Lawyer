package com.acruxcs.lawyer.ui.login

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.AppUser
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var fullname: String
    private lateinit var country: String
    private lateinit var city: String
    private lateinit var phone: String

    private lateinit var user: AppUser

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

        register_spinner_country.setAdapter(countryAdapter)

        register_spinner_country.setOnItemClickListener { adapterView, _, i, _ ->
            register_spinner_city.text.clear()
            val selected = adapterView.getItemAtPosition(i).toString()
            val cityList = countryList[selected]!!
            val cityAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ArrayList(cityList)
            ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
            register_spinner_city.setAdapter(cityAdapter)
            Utils.hideKeyboard(requireContext(), requireView())
        }

        register_spinner_city.setOnItemClickListener { _, _, _, _ ->
            Utils.hideKeyboard(requireContext(), requireView())
        }

        register_button_register.setOnClickListener {
            register()
        }
    }

    private fun register() {
        email = register_edit_email.text.toString().trim()
        password = register_edit_password.text.toString().trim()
        fullname = register_edit_full_name.text.toString().trim()
        phone = register_edit_phone.text.toString().trim()
        country = register_spinner_country.editableText.toString()
        city = register_spinner_city.editableText.toString()
        if (isValid()) {
            if (!register_checkbox_lawyer.isChecked) {
                user = User(
                    email, fullname, country, city, phone
                )
                createAccount(user as User)
            } else {
                user = Lawyer(
                    email, fullname, country, city, phone
                )
                createAccount(user as Lawyer)
            }
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        if (email.isEmpty()) {
            register_layout_edit_email.error = getString(R.string.empty_field)
            register_edit_email.requestFocus()
            valid = false
        }
        if (password.isEmpty()) {
            register_layout_edit_password.error = getString(R.string.empty_field)
            register_edit_password.requestFocus()
            valid = false
        }
        if (password.length < MIN_PASS_LENGTH) {
            register_layout_edit_password.error = getString(R.string.empty_field)
            register_edit_password.requestFocus()
            valid = false
        }
        if (fullname.isEmpty()) {
            register_layout_edit_full_name.error = getString(R.string.empty_field)
            register_edit_full_name.requestFocus()
            valid = false
        }
        if (phone.isEmpty()) {
            register_layout_edit_phone.error = getString(R.string.empty_field)
            register_edit_phone.requestFocus()
            valid = false
        }
        if (country.isEmpty()) {
            register_layout_edit_country.error = getString(R.string.empty_field)
            register_spinner_country.requestFocus()
            valid = false
        }
        if (city.isEmpty()) {
            register_layout_edit_city.error = getString(R.string.empty_field)
            register_spinner_city.requestFocus()
            valid = false
        }
        return valid
    }

    private fun createAccount(user: AppUser) {
        viewModel.firebaseAuth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    user.uid = task.result?.user!!.uid
                    viewModel.createNewUser(user)
                    if (register_checkbox_lawyer.isChecked) {
                        viewModel.setLawyer(user as Lawyer)
                    }
                    viewModel.setUser(user as User)

                    requireView().findNavController()
                        .navigate(
                            R.id.action_registerFragment_to_mainFragment,
                            bundleOf("isNewUser" to true)
                        )
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        requireActivity(), task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}

//TODO when selecting country remove edit ability, return when choosing again
