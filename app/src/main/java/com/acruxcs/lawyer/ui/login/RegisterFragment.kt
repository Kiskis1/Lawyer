package com.acruxcs.lawyer.ui.login

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.utils.Utils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val TAG = this::class.java.simpleName

    private val viewModel: LoginViewModel by viewModels()

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
        val email = register_edit_email.text.toString().trim()
        val password = register_edit_password.text.toString().trim()
        val nickname = register_edit_nickname.text.toString().trim()
        val country = register_spinner_country.editableText.toString()
        val city = register_spinner_city.editableText.toString()

        if (email.isEmpty()) {
            register_layout_edit_email.error = getString(R.string.empty_field)
            register_edit_email.requestFocus()
            return
        }
        if (password.isEmpty()) {
            register_layout_edit_password.error = getString(R.string.empty_field)
            register_edit_password.requestFocus()
            return
        }
        if (password.length < 6) {
            register_layout_edit_password.error = getString(R.string.empty_field)
            register_edit_password.requestFocus()
            return
        }
        if (nickname.isEmpty()) {
            register_layout_edit_nickname.error = getString(R.string.empty_field)
            register_edit_nickname.requestFocus()
            return
        }
        if (country.isEmpty()) {
            register_layout_edit_country.error = getString(R.string.empty_field)
            register_spinner_country.requestFocus()
            return
        }
        if (city.isEmpty()) {
            register_layout_edit_city.error = getString(R.string.empty_field)
            register_spinner_city.requestFocus()
            return
        }
        val user = User(
            email, password, nickname, country, city
        )

        createAccount(user)
    }

    private fun createAccount(user: User) {
        viewModel.firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    viewModel.createNewUser(user)
                    requireView().findNavController()
                        .navigate(R.id.action_registerFragment_to_mainFragment)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        requireActivity(), task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}