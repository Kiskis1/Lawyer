package com.acruxcs.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
@Keep
data class Case(
    var shortDesc: String = "",
    var court: String = "",
    var area: String = "",
    var type: String = "",
    var outcome: String = "",
    var date: Long = 0,
    var id: String = UUID.randomUUID().toString().replace("-", "").toUpperCase(),
    var user: String = "",
) : Parcelable
