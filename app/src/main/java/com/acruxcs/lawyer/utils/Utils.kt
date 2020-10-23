package com.acruxcs.lawyer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentManager
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.ui.QuestionDialog
import java.io.IOException

object Utils {

    const val ARG_LAWYER = "lawyer"
    private const val SHARED_KEY = "userdata"
    const val SHARED_USER_DATA = "user"
    const val SHARED_DARK_MODE_ON = "dark_mode"
    const val MIN_PASS_LENGTH = 6
    lateinit var preferences: SharedPreferences

    fun init(activity: Activity) {
        preferences = activity.getSharedPreferences(SHARED_KEY, 0)
    }

    fun showCallDialog(context: Context, item: Lawyer) {
        val dialog = AlertDialog.Builder(context)
        dialog.setMessage(R.string.call_dialog_message)
        dialog.setTitle(R.string.call_dialog_title)
        dialog.setPositiveButton(R.string.call) { _, _ ->
            val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", item.phone, null))
            context.startActivity(intent)
        }
        dialog.setNegativeButton(R.string.cancel) { d, _ ->
            d.cancel()
        }
        dialog.create().show()
    }

    fun showQuestionDialog(manager: FragmentManager, item: Lawyer) {
        QuestionDialog.newInstance(item).show(manager, "Question")
    }

    fun getJsonFromAssets(context: Context, fileName: String): String {
        val jsonString: String
        jsonString = try {
            val stream = context.assets.open(fileName)
            val size = stream.available()
            val buffer = ByteArray(size)
            stream.read(buffer)
            stream.close()
            String(buffer, charset("UTF-8"))
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
        return jsonString
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
}
