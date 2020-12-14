package com.acruxcs.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class User(
    var email: String = "",
    var fullname: String = "",
    var country: String = "",
    var city: String = "",
    var phone: String = "",
    var uid: String = "",
    var role: UserTypes = UserTypes.User,
    var address: String = "",
    var specialization: String = "",
    var education: String = "",
    var experience: Int = 0,
    var wonCases: Int = 0,
    var workingHours: WorkingHours? = null,
) : Parcelable
