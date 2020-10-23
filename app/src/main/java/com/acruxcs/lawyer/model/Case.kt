package com.acruxcs.lawyer.model

import java.util.Date

data class Case(
    var date: Date = Date(),
    var area: String = "",
    var type: String = "",
    var outcome: String = "",
    var shortDesc: String = "",
)
