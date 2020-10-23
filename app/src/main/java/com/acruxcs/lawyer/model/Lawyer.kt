package com.acruxcs.lawyer.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Lawyer(
    var email: String = "",
    var name: String = "",
    var country: String = "",
    var phone: String = "",
    var city: String = "",
    var specialization: String = "",
    var education: String = "",
    var experience: Int = 0,
    var wonCases: Int = 0,
    var role: String = "lawyer",
) : Parcelable
