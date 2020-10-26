package com.acruxcs.lawyer.model

data class User(
    override var email: String = "",
    override var fullname: String = "",
    override var country: String = "",
    override var city: String = "",
    override var phone: String = "",
    override var uid: String = "",
    override var role: String = "user"
) : AppUser
