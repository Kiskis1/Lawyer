package com.acruxcs.lawyer.model

import java.util.UUID

data class Reservation(
    var id: String = UUID.randomUUID().toString().replace("-", "").toUpperCase(),
    var date: Long = 0,
    var reason: String = "",
    var lawyer: String = "",
    var user: String = "",
)
