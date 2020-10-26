package com.acruxcs.lawyer.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Parcelize
@Keep
data class Lawyer(
    override var email: String = "",
    override var fullname: String = "",
    override var country: String = "",
    override var city: String = "",
    override var phone: String = "",
    override var uid: String = "",
    override var role: String = "lawyer",
    var specialization: String = "",
    var education: String = "",
    var experience: Int = 0,
    var wonCases: Int = 0,
) : Parcelable, AppUser
