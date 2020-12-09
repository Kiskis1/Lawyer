package com.acruxcs.lawyer.model

import java.util.UUID

data class Question(
    var description: String = "",
    var country: String = "",
    var city: String = "",
    var phone: String = "",
    var fullname: String = "",
    var destinationEmail: String = "",
    var sender: String = "",
    var id: String = UUID.randomUUID().toString().replace("-", "").toUpperCase(),
)
