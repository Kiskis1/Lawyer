package com.acruxcs.lawyer.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.ui.QuestionDialog

class Utils {

    companion object {
        fun showCallDialog(context: Context) {
            val dialog = AlertDialog.Builder(context)
            dialog.setMessage(R.string.call_dialog_message)
            dialog.setTitle(R.string.call_dialog_title)
            dialog.setPositiveButton(R.string.call) { _, _ ->
                val phone = "+37060000000"
                val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null))
                context.startActivity(intent)
            }
            dialog.setNegativeButton(R.string.cancel) { d, _ ->
                d.cancel()
            }
            dialog.create().show()
        }

        fun showQuestionDialog(manager: FragmentManager) {
            QuestionDialog().show(manager, "Question")
        }
    }
}