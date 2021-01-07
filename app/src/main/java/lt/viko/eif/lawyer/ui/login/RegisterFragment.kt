package lt.viko.eif.lawyer.ui.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.crazylegend.viewbinding.viewBinding
import com.yariksoffice.lingver.Lingver
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.databinding.FragmentRegisterBinding
import lt.viko.eif.lawyer.model.User
import lt.viko.eif.lawyer.model.UserTypes
import lt.viko.eif.lawyer.repository.SharedPrefRepository
import lt.viko.eif.lawyer.repository.SharedPrefRepository.SHARED_LOGGED_IN
import lt.viko.eif.lawyer.repository.SharedPrefRepository.preferences
import lt.viko.eif.lawyer.utils.Utils
import lt.viko.eif.lawyer.utils.Utils.MIN_PASS_LENGTH
import lt.viko.eif.lawyer.utils.Utils.checkFieldIfEmpty
import lt.viko.eif.lawyer.utils.Utils.getCitiesByCountry
import lt.viko.eif.lawyer.utils.Utils.yes
import java.util.regex.Pattern

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

    private var selectedCountry = ""

    private val regex = "^([\\w]{3,})+\\s+([\\w\\s]{3,})+$"
    private val pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProgressLayout = (activity as MainActivity).binding.progressBar.progressLayout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding) {
            spinnerCountry.setAdapter(Utils.getCountryAdapter(requireContext()))

            spinnerCountry.setOnItemClickListener { adapterView, _, i, _ ->
                selectedCountry = adapterView.getItemAtPosition(i).toString()
                spinnerCity.setAdapter(Utils.getCityAdapter(requireContext(), selectedCountry))
                spinnerCity.isEnabled = true
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
                Lingver.getInstance()
                    .setLocale(requireContext(), Utils.convertToLocaleCode(selectedCountry))
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
            if (!TextUtils.isEmpty(editEmail.text!!.trim()) &&
                !Patterns.EMAIL_ADDRESS.matcher(editEmail.text!!.trim()).matches()
            ) {
                layoutEmail.error = getString(R.string.error_invalid_email)
                editEmail.requestFocus()
                valid = false
            } else layoutEmail.error = null
            checkFieldIfEmpty(
                editPassword, layoutPassword, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editFullName, layoutFullName, requireContext()
            ).yes { valid = false }
            if (!TextUtils.isEmpty(editFullName.text!!.trim()) &&
                !pattern.matcher(editFullName.text!!.trim()).matches()
            ) {
                layoutFullName.error =
                    getString(R.string.error_invalid_name)
                editFullName.requestFocus()
                valid = false
            } else layoutFullName.error = null
            checkFieldIfEmpty(
                editPhone, layoutPhone, requireContext()
            ).yes { valid = false }
            if (password.length < MIN_PASS_LENGTH) {
                layoutPassword.error =
                    getString(R.string.error_password_not_long_enough)
                editPassword.requestFocus()
                valid = false
            } else layoutPassword.error = null

            checkFieldIfEmpty(
                spinnerCountry, layoutCountry, requireContext()
            ).yes {
                valid = false
            }
            checkFieldIfEmpty(spinnerCity, layoutCity, requireContext()).yes {
                valid = false
            }

            if (!spinnerCity.editableText.contains("N/A") &&
                !resources.getStringArray(getCitiesByCountry(selectedCountry)).contains(city)
            ) {
                layoutCity.error = getString(R.string.error_invalid_city)
                spinnerCity.requestFocus()
                spinnerCity.editableText.clear()
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
                    MainActivity.user.postValue(user)
                    preferences.edit {
                        this.putBoolean(SHARED_LOGGED_IN, true)
                        this.putStringSet(SharedPrefRepository.SHARED_AUTH_PROVIDER,
                            Utils.getProviderIdSet(task.result?.user!!))
                    }
                    activityProgressLayout.visibility = View.GONE
                    val dir = RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
                    findNavController().navigate(dir)
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
