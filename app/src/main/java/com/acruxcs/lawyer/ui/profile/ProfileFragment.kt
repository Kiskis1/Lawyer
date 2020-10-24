package com.acruxcs.lawyer.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
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
import kotlinx.android.synthetic.main.fragment_profile_lawyer.*
import kotlinx.android.synthetic.main.fragment_profile_user.profile_button_edit_picture
import kotlinx.android.synthetic.main.fragment_profile_user.profile_button_logout
import kotlinx.android.synthetic.main.fragment_profile_user.profile_edit_password
import kotlinx.android.synthetic.main.fragment_profile_user.profile_layout_password
import kotlinx.android.synthetic.main.fragment_profile_user.profile_switch_dark_mode

class ProfileFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var lawyersCasesAdapter: LawyersCaseAdapter
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
            throw NotImplementedError()
        }
        profile_layout_password.setEndIconOnClickListener {
            val password = profile_edit_password.text.toString().trim()
            if (password.isEmpty()) {
                profile_layout_password.error = getString(R.string.empty_field)
                profile_edit_password.requestFocus()
                return@setEndIconOnClickListener
            } else if (password.length < MIN_PASS_LENGTH) {
                profile_layout_password.error = getString(R.string.password_not_long_enough)
                profile_edit_password.requestFocus()
                return@setEndIconOnClickListener
            }
            viewModel.updatePassword(password)
            Utils.hideKeyboard(requireContext(), requireView())
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
        profile_fab_add_case?.setOnClickListener {
            NewCaseDialog(this).show(parentFragmentManager, "new_case")
        }

        lawyersCasesAdapter = LawyersCaseAdapter()
        profile_recycler.adapter = lawyersCasesAdapter
        lawyersViewModel.getLawyersCases().observe(viewLifecycleOwner, {
            list.clear()
            list.addAll(it)
            lawyersCasesAdapter.swapData(list)
        })
    }

    private fun logout() {
        viewModel.firebaseAuth.signOut()
        LoginManager.getInstance()?.logOut()
        (activity as MainActivity).googleSignInClient.signOut()
        requireView().findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
    }

    private fun handleStatus(status: Status?) {
        when (status) {
            Status.INVALID_URI -> Toast.makeText(
                context,
                "Unable to load the photo",
                Toast.LENGTH_SHORT
            ).show()
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
            else -> Toast.makeText(
                context,
                "Something went wrong, please try again!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
