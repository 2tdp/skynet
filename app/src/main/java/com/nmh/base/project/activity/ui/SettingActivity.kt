package com.nmh.base.project.activity.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.activity.OnBackPressedCallback
import com.nlbn.ads.util.AppOpenManager
import com.nmh.base.project.activity.ui.base.BaseActivity
import com.nmh.base.project.activity.ui.language.LanguageActivity
import com.nmh.skynet.callback.ICallBackCheck
import com.nmh.base.project.databinding.ActivitySettingBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.IS_RATED
import com.nmh.base.project.helpers.IS_SHOW_BACK
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.ActionUtils
import com.nmh.base.project.utils.UtilsRate

class SettingActivity : BaseActivity<ActivitySettingBinding>(ActivitySettingBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    @SuppressLint("CommitPrefEdits")
    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        if (DataLocalManager.getBoolean(IS_RATED, true))
            binding.cvRate.gone()

        binding.ivBack.setOnUnDoubleClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.cvLang.setOnUnDoubleClickListener {
            DataLocalManager.setBoolean(IS_SHOW_BACK, true)
            startIntent(Intent(this, LanguageActivity::class.java), false)
        }
        binding.cvRate.setOnUnDoubleClickListener {
            AppOpenManager.getInstance().disableAppResumeWithActivity(SettingActivity::class.java)
            UtilsRate.showRate(this, false, object : ICallBackCheck {
                override fun check(isCheck: Boolean) {
                    if (isCheck) binding.cvRate.gone()
                }
            })
        }
        binding.cvShare.setOnUnDoubleClickListener { ActionUtils.shareApp(this) }
        binding.cvPolicy.setOnUnDoubleClickListener { ActionUtils.openPolicy(this) }
    }

    override fun onResume() {
        super.onResume()

        AppOpenManager.getInstance().enableAppResumeWithActivity(SettingActivity::class.java)
    }
}