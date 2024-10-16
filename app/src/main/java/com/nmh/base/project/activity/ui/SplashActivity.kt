package com.nmh.base.project.activity.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.BuildConfig
import com.nmh.base.project.R
import com.nmh.base.project.activity.data.db.LanguageModel
import com.nmh.base.project.activity.ui.base.BaseActivity
import com.nmh.base.project.activity.ui.language.LanguageActivity
import com.nmh.base.project.databinding.ActivitySplashBinding
import com.nmh.base.project.helpers.CURRENT_LANGUAGE
import com.nmh.base.project.helpers.IS_SHOW_BACK
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.AdsConfig
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    private var interCallback: AdCallback? = null

    override fun setUp() {
        if (DataLocalManager.getLanguage(CURRENT_LANGUAGE) == null) {
            DataLocalManager.setLanguage(
                CURRENT_LANGUAGE,
                LanguageModel("English", "flag_language", Locale.ENGLISH, true)
            )
        }

        if (haveNetworkConnection()) {
            CoroutineScope(Dispatchers.IO).launch {
                val remote = async { loadRemoteConfig() }
                if (remote.await())
                    withContext(Dispatchers.Main) {
                        interCallback = object : AdCallback() {
                            override fun onNextAction() {
                                super.onNextAction()
                                startActivity()
                            }
                        }

                        val consentHelper = ConsentHelper.getInstance(this@SplashActivity)
                        if (!consentHelper.canLoadAndShowAds()) consentHelper.reset()

                        consentHelper.obtainConsentAndShow(this@SplashActivity) {
                            //load trước native language
                            AdsConfig.loadNativeLanguage(this@SplashActivity)
                            AdsConfig.loadNativeLanguageSelect(this@SplashActivity)

                            Admob.getInstance().loadSplashInterAds2(this@SplashActivity, getString(R.string.inter_splash), 0, interCallback)
                        }
                    }
            }
        } else Handler(Looper.getMainLooper()).postDelayed({ startActivity() }, 1500)
    }

    private suspend fun loadRemoteConfig(): Boolean {
        return suspendCoroutine { continuation ->

            val configSetting = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.Minimum_Fetch)
                .build()

            val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
                setConfigSettingsAsync(configSetting)
            }

            remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults) /*file này lấy trên firebase*/

            remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //chỗ này sẽ get value key remote (làm và lấy theo trên link excel trên nhóm a Huy gửi, nhân bản ra sheet riêng)
                }
                continuation.resume(true)
            }
        }
    }

    private fun startActivity() {
        DataLocalManager.setBoolean(IS_SHOW_BACK, false)
        startIntent(Intent(this, LanguageActivity::class.java), true)
    }
}