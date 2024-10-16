package com.nmh.base.project.activity.ui

import androidx.activity.OnBackPressedCallback
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.nativead.NativeAd
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.activity.ui.base.BaseActivity
import com.nmh.skynet.callback.ICallBackCheck
import com.nmh.base.project.databinding.ActivityActionOneBinding
import com.nmh.base.project.databinding.AdsNativeBotHorizontalMediaLeftBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.utils.AdsConfig

class OptionOneActivity: BaseActivity<ActivityActionOneBinding>(ActivityActionOneBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showInterBack()
            }
        })

        showNative()

        binding.ivBack.setOnUnDoubleClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun showInterBack() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.interBack != null && AdsConfig.checkTimeShowInter()
            && AdsConfig.isLoadFullAds() /* thêm điều kiện remote */) {
            Admob.getInstance().showInterAds(this@OptionOneActivity, AdsConfig.interBack, object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()

                    finish()
                }

                override fun onAdClosedByUser() {
                    super.onAdClosedByUser()
                    AdsConfig.interBack = null
                    AdsConfig.lastTimeShowInter = System.currentTimeMillis()
                    AdsConfig.loadInterBack(this@OptionOneActivity)
                }
            })
        } else {
            /*nếu không có kịch bản native_back thì finish() luôn*/
            /*ẩn tất cả các ads đang có trên màn hình(banner, native) để show dialog*/
            binding.rlNative.gone()
            showDialogBack(object : ICallBackCheck {
                override fun check(isCheck: Boolean) {
                    /*hiện tất cả các ads đang có trên màn hình(banner, native) khi dialog ẩn đi*/
                    binding.rlNative.visible()
                }
            })
        }
    }

    private fun showNative() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds() /*thêm điều kiện remote*/) {
            binding.rlNative.visible()
            AdsConfig.nativeAll?.let {
                pushViewAds(it)
            } ?: run {
                Admob.getInstance().loadNativeAd(this, getString(R.string.native_all),
                    object : NativeCallback() {
                        override fun onNativeAdLoaded(nativeAd: NativeAd) {
                            pushViewAds(nativeAd)
                        }

                        override fun onAdFailedToLoad() {
                            binding.frNativeAds.removeAllViews()
                        }
                    }
                )
            }
        } else binding.rlNative.gone()
    }

    private fun pushViewAds(nativeAd: NativeAd) {
        val adView = AdsNativeBotHorizontalMediaLeftBinding.inflate(layoutInflater)

        if (!AdsConfig.isLoadFullAds())
            adView.adUnitContent.setBackgroundResource(R.drawable.bg_native)
        else adView.adUnitContent.setBackgroundResource(R.drawable.bg_native_no_stroke)

        binding.frNativeAds.removeAllViews()
        binding.frNativeAds.addView(adView.root)
        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView.root)
    }
}