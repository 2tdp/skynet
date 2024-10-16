package com.nmh.base.project.activity.ui

import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.nativead.NativeAd
import com.nlbn.ads.banner.BannerPlugin
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.activity.ui.base.BaseActivity
import com.nmh.base.project.activity.ui.permission.PermissionSheet
import com.nmh.skynet.callback.ICallBackCheck
import com.nmh.base.project.databinding.ActivityMainBinding
import com.nmh.base.project.databinding.AdsNativeBotHorizontalMediaLeftBinding
import com.nmh.base.project.extensions.checkAllPerGrand
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.utils.AdsConfig
import com.nmh.base.project.utils.AdsConfig.cbFetchInterval
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    @Inject lateinit var permissionSheet: PermissionSheet

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.banner.gone()
                binding.rlNative.gone()
                showDialogExit(object : ICallBackCheck {
                    override fun check(isCheck: Boolean) {
                        /*thêm đủ điều kiện như khi show ads(tránh trường hợp ko đủ điều kiện show nhưng vẫn show => hiện loading)*/
                        binding.banner.visible()
                        binding.rlNative.visible()
                    }
                })
            }
        })

        permissionSheet.apply {
            isDone = object : ICallBackCheck {
                override fun check(isCheck: Boolean) {
                    //trong đây chạy action trước đó sau khi cấp đủ quyền
                }
            }
            isDismiss = object : ICallBackCheck {
                override fun check(isCheck: Boolean) {
                    /*thêm đủ điều kiện như khi show ads(tránh trường hợp ko đủ điều kiện show nhưng vẫn show => hiện loading)*/
                    binding.rlNative.visible()
                    binding.banner.visible()
                }
            }
        }

        loadBanner()
        showNativeHome()
        AdsConfig.loadNativeExitApp(this@MainActivity)
        AdsConfig.loadNativeAll(this@MainActivity)
        /*load tất cả inter có trong app tại màn home*/
        AdsConfig.loadInterHome(this@MainActivity)
        AdsConfig.loadInterBack(this@MainActivity)

        setUpLayout()
        evenClick()
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(MainActivity::class.java)

        /*check lại full quyền, đồng thời chỉnh sửa UI button Dialog Permission*/
        if (!permissionSheet.checkPer()) permissionSheet.loadNative()
    }

    private fun loadBanner() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()) {
            val config = BannerPlugin.Config()
            config.defaultRefreshRateSec = cbFetchInterval /*cbFetchInterval lấy theo remote*/
            config.defaultCBFetchIntervalSec = cbFetchInterval

            if (true /*thêm biến check remote, thường là switch_banner_collapse*/) {
                config.defaultAdUnitId = getString(R.string.banner_collapse_all)
                config.defaultBannerType = BannerPlugin.BannerType.CollapsibleBottom
            } else if (true /*thêm biến check remote, thường là banner_all*/) {
                config.defaultAdUnitId = getString(R.string.banner_all)
                config.defaultBannerType = BannerPlugin.BannerType.Adaptive
            } else {
                binding.banner.gone()
                return
            }
            Admob.getInstance().loadBannerPlugin(this, findViewById(R.id.banner), findViewById(R.id.shimmer), config)
        } else binding.banner.gone()
    }

    private fun setUpLayout() {
        showLoading()
        Handler(Looper.getMainLooper()).postDelayed({ hideLoading() }, 1500)
    }

    private fun evenClick() {
        binding.ivSetting.setOnUnDoubleClickListener {
            startIntentForResult(startForResult, SettingActivity::class.java.name, false)
        }

        binding.tvCheck.setOnUnDoubleClickListener {
            if (!checkAllPerGrand()) {
                if (!permissionSheet.isShowing) {
                    binding.rlNative.gone()
                    binding.banner.gone()
                    permissionSheet.showDialog()
                }
            }
        }
        binding.tvShowNativeAll.setOnUnDoubleClickListener {
            showInterHome(OptionOneActivity::class.java.name)
        }
    }

    private fun startActivity(className: String) {
        startIntentForResult(startForResult, className, false)
    }

    private fun showInterHome(className: String) {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds()
            && AdsConfig.interHome != null && AdsConfig.checkTimeShowInter()
            && AdsConfig.isLoadFullAds() /* thêm điều kiện remote */){
            Admob.getInstance().showInterAds(this@MainActivity, AdsConfig.interHome, object : AdCallback() {
                override fun onNextAction() {
                    super.onNextAction()

                    startActivity(className)
                }

                override fun onAdClosedByUser() {
                    super.onAdClosedByUser()
                    AdsConfig.interHome = null
                    AdsConfig.lastTimeShowInter = System.currentTimeMillis()
                    AdsConfig.loadInterHome(this@MainActivity)
                }
            })
        } else startActivity(className)
    }

    private fun showNativeHome() {
        if (haveNetworkConnection() && ConsentHelper.getInstance(this).canRequestAds() /*thêm điều kiện remote*/) {
            binding.rlNative.visible()
            AdsConfig.nativeHome?.let {
                pushViewAds(it)
            } ?: run {
                Admob.getInstance().loadNativeAd(this, getString(R.string.native_home),
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

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (AdsConfig.nativeAll == null) AdsConfig.loadNativeAll(this)

            showNativeHome()

            AdsConfig.loadNativeBackHome(this@MainActivity)
        }
}