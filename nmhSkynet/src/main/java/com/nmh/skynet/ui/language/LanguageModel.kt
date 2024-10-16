package com.nmh.skynet.ui.language

import java.util.Locale

data class LanguageModel(
    var name: String = "",
    var uri: String = "",
    var locale: Locale = Locale.ENGLISH,
    var isCheck: Boolean
)