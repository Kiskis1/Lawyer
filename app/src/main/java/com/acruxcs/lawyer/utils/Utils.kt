package com.acruxcs.lawyer.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.model.Lawyer
import com.acruxcs.lawyer.ui.QuestionDialog
import java.io.IOException

object Utils {

    const val ARG_LAWYER = "lawyer"
    const val SHARED_KEY = "userdata"
    const val SHARED_USER_DATA = "user"

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

    fun <E> MutableList<E>.removeFirst(length: Int): MutableList<E> {
        if (length in 1..size) {
            subList(0, length).clear()
        }
        return this
    }

    fun convertString2Map(mapAsString: String): Map<String, String> {
        return mapAsString.split(", ").associate {
            val (left, right) = it.split("=")
            left to right
        }.filterValues { it != "" }
    }

    fun convertMap2String(map: Map<String, String>): String {
        val mapAsString = StringBuilder("")
        for (key in map.keys) {
            mapAsString.append(key + "=" + map[key] + ", ")
        }
        mapAsString.delete(mapAsString.length - 2, mapAsString.length).append("")
        return mapAsString.toString()
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
}
