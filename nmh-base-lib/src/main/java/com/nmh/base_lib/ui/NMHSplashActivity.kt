package com.nmh.base_lib.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.nmh.base_lib.R
import com.nmh.base_lib.databinding.ActivitySplashNmhBinding
import com.nmh.base_lib.extensions.gone
import com.nmh.base_lib.extensions.visible
import com.nmh.base_lib.ui.base.BaseActivity

@SuppressLint("CustomSplashScreen")
abstract class NMHSplashActivity: BaseActivity<ActivitySplashNmhBinding>(ActivitySplashNmhBinding::inflate) {

    protected abstract fun background(): Int
    @DrawableRes
    protected abstract fun logo(): Int
    protected open fun isHideLogo(): Boolean = false
    protected open fun sizeLogo(): IntArray? = null
    protected abstract fun strAppName(): String
    protected open fun sizeTextAppName(): Float = resources.getDimension(com.intuit.ssp.R.dimen._15ssp)
    protected abstract fun colorTextAppName(): Int
    protected open fun isHideAppName(): Boolean = false
    protected open fun fontAppName(): Typeface? = null
    @ColorInt
    protected abstract fun colorBgLoading(): Int
    @ColorInt
    protected abstract fun colorProgressLoading(): Int

    override fun setUp() {
        //background
        try {
            binding.root.setBackgroundResource(background())
        } catch (e: Exception) {
            binding.root.setBackgroundColor(background())
        }

        //image logo
        binding.iv.apply {
            if (isHideLogo()) gone() else visible()
            sizeLogo()?.let {
                layoutParams.width = it[0]
                layoutParams.height = it[1]
            }
            try {
                setBackgroundResource(logo())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        //text app name
        binding.tv.apply {
            text = strAppName()
            textSize = sizeTextAppName()
            setTextColor(colorTextAppName())
            if (isHideAppName()) gone() else visible()
            fontAppName()?.let { typeface = it }
        }

        //loading
        binding.vLoading.apply {
            setColorBackground(colorBgLoading())
            setColorProgress(colorProgressLoading())
        }
    }
}