package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentQuestionBinding
import com.acruxcs.lawyer.model.Question
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.utils.Utils
import com.acruxcs.lawyer.utils.Utils.checkFieldIfEmpty
import com.acruxcs.lawyer.utils.Utils.getCitiesByCountry
import com.acruxcs.lawyer.utils.Utils.getCityAdapter
import com.acruxcs.lawyer.utils.Utils.getCountryAdapter
import com.acruxcs.lawyer.utils.Utils.yes
import com.crazylegend.viewbinding.viewBinding

class QuestionFragment : Fragment(R.layout.fragment_question) {
    private var question: Question? = null
    private var lawyer: User? = null
    private val binding by viewBinding(FragmentQuestionBinding::bind)
    private var tagas: String? = null
    private lateinit var selectedCountry: String
    private val args: QuestionFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lawyer = args.lawyer
        tagas = args.tag
        question = args.question
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditTexts()
        with(binding) {
            if (tagas != null && tagas == "edit_question") {
                toolbar.toolbar.setTitle(R.string.dialog_title_edit)
                editDescription.setText(question?.description)
                editCountry.setText(question?.country)
                editCity.setText(question?.city)
                editPhone.setText(question?.phone)
                editName.setText(question?.fullname)
                editEmail.setText(question?.email)
            } else {
                question = Question()
                toolbar.toolbar.setTitle(R.string.dialog_title_question)
            }
            toolbar.toolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                setOnMenuItemClickListener(toolbarMenuClickListener)
            }
            editCountry.apply {
                setAdapter(getCountryAdapter(requireContext()))
                setOnItemClickListener { adapterView, _, i, _ ->
                    selectedCountry = adapterView.getItemAtPosition(i).toString()
                    editCity.setAdapter(getCityAdapter(requireContext(), selectedCountry))
                    editCity.isEnabled = true
                    Utils.hideKeyboard(requireContext(), requireView())
                }
            }
            if (MainActivity.user.value!!.country == "N/A")
                editCity.isEnabled = false
            else editCity.setAdapter(getCityAdapter(requireContext(),
                MainActivity.user.value!!.country))
        }
    }

    private val toolbarMenuClickListener = Toolbar.OnMenuItemClickListener { item ->
        with(binding) {
            when (item.itemId) {
                R.id.action_confirm -> {
                    if (isValid()) {
                        question!!.description = editDescription.text.toString().trim()
                        question!!.country = editCountry.text.toString().trim()
                        question!!.city = editCity.text.toString().trim()
                        question!!.phone = editPhone.text.toString().trim()
                        question!!.email = editEmail.text.toString().trim()
                        question!!.fullname = editName.text.toString().trim()
                        if (tagas == null) question!!.destination = lawyer!!.uid
                        question!!.sender = MainActivity.user.value!!.uid
                        findNavController().previousBackStackEntry?.savedStateHandle?.set("question",
                            question)
                        Utils.hideKeyboard(requireContext(), binding.root)
                        findNavController().navigateUp()
                    }
                    true
                }
                else -> false
            }
        }

    }

    private fun setupEditTexts() {
        with(binding) {
            editCountry.setText(MainActivity.user.value?.country)
            editCity.setText(MainActivity.user.value?.city)
            editPhone.setText(MainActivity.user.value?.phone)
            editName.setText(MainActivity.user.value?.fullname)
            editEmail.setText(MainActivity.user.value?.email)
        }
    }

    private fun isValid(): Boolean {
        var valid = true
        with(binding) {
            checkFieldIfEmpty(
                editDescription, layoutDescription, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editCountry, layoutCountry, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editCity, layoutCity, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editPhone, layoutPhone, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editName, layoutName, requireContext()
            ).yes { valid = false }
            checkFieldIfEmpty(
                editEmail, layoutEmail, requireContext()
            ).yes { valid = false }
            if (!editCountry.editableText.contains("N/A") && !resources.getStringArray(
                    getCitiesByCountry(editCountry.editableText.toString().trim()))
                    .contains(editCity.editableText.toString().trim())
            ) {
                valid = false
                editCity.editableText.clear()
            }
        }
        return valid
    }
}
