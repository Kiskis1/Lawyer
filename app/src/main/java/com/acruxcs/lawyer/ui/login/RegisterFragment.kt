package com.acruxcs.lawyer.ui.login

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentRegisterBinding
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.UserTypes
import com.acruxcs.lawyer.ui.main.MainViewModel
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.MIN_PASS_LENGTH
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.checkSpinnerIfEmpty
import com.acruxcs.lawyer.utils.Utils.countriesMapType
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding
import com.google.gson.Gson

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val viewModel: MainViewModel by activityViewModels()
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

    private val jsonString by lazy {
        Utils.getJsonFromAssets(
            requireContext(), "countries.min.json"
        )
    }
    private val countryList by lazy {
        Gson().fromJson<Map<String, List<String>>>(jsonString, countriesMapType).toSortedMap()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProgressLayout = (activity as MainActivity).binding.progressLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val countryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            ArrayList(countryList.keys)
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        with(binding) {
            registerSpinnerCountry.setAdapter(countryAdapter)

            registerSpinnerCountry.setOnItemClickListener { adapterView, _, i, _ ->
                registerSpinnerCity.text.clear()
                selectedCountry = adapterView.getItemAtPosition(i).toString()
                val cityList = countryList[selectedCountry]!!
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
                activityProgressLayout.visibility = View.VISIBLE
                createAccount(user)
            }
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(
                registerEditEmail, registerLayoutEditEmail, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                registerEditPassword, registerLayoutEditPassword, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                registerEditFullName, registerLayoutEditFullName, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                registerEditPhone, registerLayoutEditPhone, requireContext()
            ).yes { valid = false }
            if (password.length < MIN_PASS_LENGTH) {
                registerLayoutEditPassword.error = getString(R.string.password_not_long_enough)
                registerEditPassword.requestFocus()
                valid = false
            } else registerLayoutEditPassword.error = null

            checkSpinnerIfEmpty(
                registerSpinnerCountry, registerLayoutEditCountry, requireContext()
            ).yes {
                valid = false
            }
            checkSpinnerIfEmpty(registerSpinnerCity, registerLayoutEditCity, requireContext()).yes {
                valid = false
            }

            if (!countryList[selectedCountry]?.contains(city)!!) {
                registerLayoutEditCity.error = getString(R.string.invalid_city)
                registerSpinnerCity.requestFocus()
                valid = false
            } else registerLayoutEditCity.error = null
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

//TODO when selecting country remove edit ability, return when choosing again
