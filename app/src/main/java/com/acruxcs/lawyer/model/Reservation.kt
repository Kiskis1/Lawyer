package com.acruxcs.lawyer.model

import java.util.Locale
import java.util.UUID

data class Reservation(
    var id: String = UUID.randomUUID().toString().replace("-", "").toUpperCase(Locale.ENGLISH),
    var date: String = "",
    var time: String = "",
    var reason: String = "",
    var lawyer: User? = null,
    var user: String = "",
    var dateLawyer: String = "",
)
