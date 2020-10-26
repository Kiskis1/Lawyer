package com.acruxcs.lawyer.model

data class User(
    var email: String = "",
    var password: String = "",
    var nickname: String = "",
    var country: String = "",
    var city: String = "",
    var uid: String = "",
    var role: String = "user"
) : AppUser
