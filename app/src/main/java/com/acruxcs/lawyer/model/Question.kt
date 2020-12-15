package com.acruxcs.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.Locale
import java.util.UUID

@Parcelize
@Keep
data class Question(
    var description: String = "",
    var country: String = "",
    var city: String = "",
    var phone: String = "",
    var fullname: String = "",
    var destination: String = "",
    var sender: String = "",
    var id: String = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ENGLISH),
) : Parcelable
