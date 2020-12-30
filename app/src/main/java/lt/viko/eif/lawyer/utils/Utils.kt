package lt.viko.eif.lawyer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import lt.viko.eif.lawyer.MainActivity
import lt.viko.eif.lawyer.R
import lt.viko.eif.lawyer.model.UserTypes
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object Utils {

    const val MIN_PASS_LENGTH = 6

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

    fun showCallDialog(context: Context, phone: String, type: UserTypes) {
        if (phone == "" || phone == "N/A") {
            Toast.makeText(context, R.string.error_no_phone_number, Toast.LENGTH_SHORT).show()
            return
        }
        val builder = AlertDialog.Builder(context)
        if (type == UserTypes.Lawyer) {
            builder.setMessage(R.string.call_dialog_message_lawyer)
            builder.setTitle(R.string.call_dialog_title_lawyer)
        } else {
            builder.setMessage(R.string.call_dialog_message_user)
            builder.setTitle(R.string.call_dialog_title_user)
        }
        builder.setPositiveButton(R.string.action_call) { _, _ ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
            context.startActivity(intent)
        }
        builder.setNegativeButton(R.string.action_cancel) { d, _ ->
            d.cancel()
        }

        val dialog = builder.create()
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnim
        dialog.show()
    }

    fun getCitiesByCountry(country: String): Int {
        return when (country) {
            "Lietuva",
            "Lithuania",
            -> R.array.Lithuania
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
        if (TextUtils.isEmpty(edit.editableText) || edit.editableText.contains("N/A")) {
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

    fun getProviderIdSet(user: FirebaseUser): MutableSet<String> {
        val set = mutableSetOf<String>()
        for (providerData in user.providerData) {
            set.add(providerData.providerId)
        }
        return set
    }

    fun getCountryAdapter(context: Context) = ArrayAdapter.createFromResource(
        context,
        R.array.Countries,
        android.R.layout.simple_dropdown_item_1line
    ).also {
        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    private val allCities by lazy {
        val array = mutableListOf<String>()
        for (country in MainActivity.appContext.resources.getStringArray(R.array.Countries)) {
            array.addAll(
                MainActivity.appContext.resources.getStringArray(getCitiesByCountry(country))
            )
        }
        array
    }

    fun getAllCityAdapter(context: Context) =
        ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            allCities
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

    fun getSpecializationAdapter(context: Context) =
        ArrayAdapter.createFromResource(
            context,
            R.array.Specializations,
            android.R.layout.simple_dropdown_item_1line
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    fun getPaymentTypeAdapter(context: Context) =
        ArrayAdapter.createFromResource(
            context,
            R.array.PaymentTypes,
            android.R.layout.simple_dropdown_item_1line
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

    fun getCityAdapter(context: Context, country: String) = ArrayAdapter.createFromResource(
        context,
        getCitiesByCountry(country),
        android.R.layout.simple_dropdown_item_1line
    ).also {
        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    fun getExperienceAdapter(context: Context): ArrayAdapter<String> {
        var years = arrayOf<String>()
        val now = Calendar.getInstance().get(Calendar.YEAR)
        for (i in 1950..now) {
            years += i.toString()
        }
        return ArrayAdapter(
            context,
            android.R.layout.simple_dropdown_item_1line,
            years
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
    }

    fun convertToLocaleCode(country: String) =
        when (country) {
            "Lietuva" -> "lt"
            "United Kingdom",
            "United States",
            -> "en"
            else -> "en"
        }
}

@BindingAdapter(value = ["role", "wanted"], requireAll = true)
fun goneUnless(view: View, role: UserTypes, wanted: UserTypes) {
    view.visibility = if (role == wanted) View.VISIBLE else View.GONE
}
