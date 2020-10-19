package com.acruxcs.lawyer.model

data class Question(
    var description: String = "",
    var country: String = "",
    var city: String = "",
    var phone: String = "",
    var name: String = "",
    var destinationEmail: String = ""
)