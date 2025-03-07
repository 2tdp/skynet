package com.nmh.skynet.ui.onboarding

import com.google.gson.annotations.SerializedName

data class OnBoardingModel(
    @SerializedName("strTitle")
    var strTitle: String = "",
    @SerializedName("str")
    var str: String = "",
    @SerializedName("imgPage")
    var imgPage: Int = -1,
)