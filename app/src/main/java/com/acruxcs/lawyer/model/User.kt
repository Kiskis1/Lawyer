package com.acruxcs.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Parcelize
@Keep
data class User(
    var email: String = "",
    var fullname: String = "",
    var country: String = "N/A",
    var city: String = "N/A",
    var phone: String = "N/A",
    var uid: String = "",
    var role: UserTypes = UserTypes.User,
    var address: String = "N/A",
    var specialization: String = "N/A",
    var education: String = "N/A",
    var experience: Int = 0,
    var wonCases: Int = 0,
    var workingHours: WorkingHours? = null,
    var imageRef: String = "",
    var paymentTypes: String = "",
) : Parcelable
