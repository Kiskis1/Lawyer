package com.acruxcs.lawyer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentManager
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.model.UserTypes
import com.acruxcs.lawyer.ui.QuestionDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object Utils {

    const val ARG_LAWYER = "lawyer"
    private const val SHARED_KEY = "userdata"
    const val SHARED_USER_DATA = "user"
    const val SHARED_DARK_MODE_ON = "dark_mode"
    const val SHARED_AUTH_PROVIDER = "provider"
    const val MIN_PASS_LENGTH = 6
    lateinit var preferences: SharedPreferences

    fun init(activity: Activity) {
        preferences = activity.getSharedPreferences(SHARED_KEY, 0)
    }

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

    fun showQuestionDialog(manager: FragmentManager, item: User) {
        QuestionDialog.newInstance(item).show(manager, "Question")
    }

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

    inline fun SharedPreferences.edit(
        operation:
            (SharedPreferences.Editor) -> Unit
    ) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    inline fun Boolean.yes(block: () -> Unit) = also { if (it) block() }

    fun checkFieldIfEmpty(
        edit: TextInputEditText,
        layout: TextInputLayout,
        context: Context
    ): Boolean {
        if (TextUtils.isEmpty(edit.text)) {
            layout.error = context.resources.getString(R.string.error_empty_field)
            return true
        } else layout.error = null
        return false
    }

    fun checkSpinnerIfEmpty(
        edit: AutoCompleteTextView,
        layout: TextInputLayout,
        context: Context
    ): Boolean {
        if (TextUtils.isEmpty(edit.editableText)) {
            layout.error = context.resources.getString(R.string.error_empty_field)
            edit.requestFocus()
            return true
        } else layout.error = null
        return false
    }
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, role: UserTypes?) {
    view.visibility = if (role == UserTypes.Lawyer) View.VISIBLE else View.GONE
}
