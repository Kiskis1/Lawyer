package com.acruxcs.lawyer.repository

import android.content.Context
import android.content.SharedPreferences

object SharedPrefRepository {

    lateinit var preferences: SharedPreferences

    private const val SHARED_KEY = "userdata"
    const val SHARED_DARK_MODE_ON = "dark_mode"
    const val SHARED_AUTH_PROVIDER = "provider"
    const val SHARED_LOGGED_IN = "loggedin"

    operator fun invoke(context: Context): SharedPrefRepository {
        preferences = context.getSharedPreferences(SHARED_KEY, 0)
        return this
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
