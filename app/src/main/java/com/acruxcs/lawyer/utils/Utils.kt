package com.acruxcs.lawyer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.UserTypes
import com.google.android.gms.common.util.ArrayUtils
import com.google.android.material.textfield.TextInputLayout

object Utils {

    const val ARG_LAWYER = "lawyer"
    const val MIN_PASS_LENGTH = 6

    fun showCallDialog(context: Context, item: User) {
        val dialog = AlertDialog.Builder(context)
        dialog.setMessage(R.string.call_dialog_message)
        dialog.setTitle(R.string.call_dialog_title)
        dialog.setPositiveButton(R.string.action_call) { _, _ ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.phone, null))
            context.startActivity(intent)
        }
        dialog.setNegativeButton(R.string.action_cancel) { d, _ ->
            d.cancel()
        }
        dialog.create().show()
    }

    // fun showQuestionDialog(manager: FragmentManager, item: User) {
    //     QuestionDialog.newInstance(item).show(manager, "Question")
    // }

    fun getCitiesByCountry(country: String): Int {
        return when (country) {
            "Lithuania" -> R.array.Lithuania
            "United States" -> R.array.United_States
            "United Kingdom" -> R.array.United_Kingdom
            else -> 0
        }
    }

    fun hideKeyboard(context: Context, view: View) {
        val imm: InputMethodManager =
            context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun switchDarkMode(on: Boolean) {
        if (on)
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    inline fun Boolean.yes(block: () -> Unit) = also { if (it) block() }

    fun checkFieldIfEmpty(
        edit: EditText,
        layout: TextInputLayout,
        context: Context,
    ): Boolean {
        if (TextUtils.isEmpty(edit.editableText)) {
            layout.error = context.resources.getString(R.string.error_empty_field)
            edit.requestFocus()
            return true
        } else layout.error = null
        return false
    }

    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }

    fun View.toggleVisibility() {
        visibility = if (visibility == View.VISIBLE) {
            View.INVISIBLE
        } else {
            View.VISIBLE
        }
    }

    val countryAdapter by lazy {
        ArrayAdapter.createFromResource(
            MainApplication.appContext,
            R.array.Countries,
            android.R.layout.simple_dropdown_item_1line
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    private val allCities by lazy {
        val array = mutableListOf<String>()
        for (country in MainApplication.appContext.resources.getStringArray(R.array.Countries)) {
            array.addAll(
                MainApplication.appContext.resources.getStringArray(
                    getCitiesByCountry(
                        country
                    )
                )
            )
        }
        array
    }

    val cityAdapter by lazy {
        ArrayAdapter(
            MainApplication.appContext,
            android.R.layout.simple_dropdown_item_1line,
            allCities
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    val experienceAdapter by lazy {
        ArrayAdapter(
            MainApplication.appContext,
            android.R.layout.simple_spinner_item,
            ArrayUtils.toWrapperArray(MainApplication.appContext.resources.getIntArray(R.array.Experience))
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }
    val specializationAdapter by lazy {
        ArrayAdapter.createFromResource(
            MainApplication.appContext,
            R.array.Specializations,
            android.R.layout.simple_dropdown_item_1line
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    fun getCityAdapter(country: String) = ArrayAdapter.createFromResource(
        MainApplication.appContext,
        getCitiesByCountry(country),
        android.R.layout.simple_dropdown_item_1line
    ).also {
        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, role: UserTypes?) {
    view.visibility = if (role == UserTypes.Lawyer) View.VISIBLE else View.GONE
}
