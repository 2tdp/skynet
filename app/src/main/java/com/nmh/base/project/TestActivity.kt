package com.nmh.base.project

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.nmh.base.project.R
import com.nmh.skynet.ui.NMHSplashActivity

class TestActivity: NMHSplashActivity() {

    override fun background(): Int = Color.YELLOW
    override fun logo(): Int = R.drawable.logo

    override fun strAppName(): String = getString(R.string.app_name)

    override fun sizeTextAppName(): Float = resources.getDimension(com.intuit.ssp.R.dimen._18ssp)

    override fun colorTextAppName(): Int = Color.RED

    override fun colorBgLoading(): Int = ContextCompat.getColor(this, R.color.color_DFDFDF)

    override fun colorProgressLoading(): Int = ContextCompat.getColor(this, R.color.color_0AD6F2)
}