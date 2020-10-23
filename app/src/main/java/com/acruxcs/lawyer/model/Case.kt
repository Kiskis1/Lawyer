package com.acruxcs.lawyer.model

data class Case(
    var shortDesc: String = "",
    var court: String = "",
    var area: String = "",
    var type: String = "",
    var outcome: String = "",
    var date: Long = 0,
    var user: String = "",
)
