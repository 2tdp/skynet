package com.nmh.skynet.ui.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.nmh.skynet.R
import com.nmh.skynet.databinding.DialogLoadingBinding
import com.nmh.skynet.extensions.changeLanguage
import com.nmh.skynet.extensions.createBackground
import com.nmh.skynet.extensions.setUpDialog
import com.nmh.skynet.sharepref.DataLocalManager
import com.nmh.skynet.utils.Constant.CURRENT_LANGUAGE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity<B : ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B
) : AppCompatActivity(), BaseView, CoroutineScope {

    lateinit var job: Job
    var w = 0F
    override val coroutineContext: CoroutineContext
        get() = getDispatchers() + job

    val binding: B by lazy { bindingFactory(layoutInflater) }

    private var mIsShowLoading = false
    private var loadingDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.navigationBarColor = Color.parseColor("#01ffffff")
        window.statusBarColor = Color.TRANSPARENT
        window.decorView.systemUiVisibility = hideSystemBars()

        job = Job()
        setContentView(binding.root)
        w = resources.displayMetrics.widthPixels / 100F
        setUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(changeLanguage(newBase, DataLocalManager.getLanguage(CURRENT_LANGUAGE)))
    }

    protected open fun getDispatchers(): CoroutineContext = Dispatchers.IO + job

    protected open fun getColorState(): IntArray = intArrayOf(Color.TRANSPARENT, Color.WHITE)

    protected open fun isVisible(): Boolean = true

    protected open fun isHideNavigation(): Boolean = true

    protected abstract fun setUp()

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (isHideNavigation()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.navigationBarColor = Color.parseColor("#01ffffff")
            window.statusBarColor = Color.TRANSPARENT
            window.decorView.systemUiVisibility = hideSystemBars()
        }
    }

    private fun hideSystemBars(): Int = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    override fun startIntent(nameActivity: String, isFinish: Boolean) {
        val intent = Intent().apply {
            component = ComponentName(this@BaseActivity, nameActivity)
        }
        startActivity(intent, null)
        if (isFinish) this.finish()
    }

    override fun startIntent(intent: Intent, isFinish: Boolean) {
        startActivity(intent, null)
        if (isFinish) this.finish()
    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        nameActivity: String,
        isFinish: Boolean
    ) {
        startForResult.launch(
            Intent().apply {
                component = ComponentName(this@BaseActivity, nameActivity)
            }, null
        )
        if (isFinish) this.finish()
    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        intent: Intent,
        isFinish: Boolean
    ) {
        startForResult.launch(intent, null)
        if (isFinish) this.finish()
    }

    fun checkPer(str: Array<String>): Boolean {
        if (str[0] == "") return true
        var isCheck = true
        for (i in str) {
            if (ContextCompat.checkSelfPermission(this, i) != PackageManager.PERMISSION_GRANTED)
                isCheck = false
        }

        return isCheck
    }

    fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true))
                if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName.equals("MOBILE", ignoreCase = true))
                if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }

    override fun showLoading() {
        showLoading(true)
    }

    private fun initDialog(isCancel: Boolean) {
        val bindingDialog = DialogLoadingBinding.inflate(LayoutInflater.from(this@BaseActivity))
        bindingDialog.root.createBackground(intArrayOf(Color.WHITE), 3.5f * w, -1, -1)

        loadingDialog = AlertDialog.Builder(this@BaseActivity, R.style.SheetDialog).create()
        loadingDialog?.setUpDialog(bindingDialog.root, isCancel)

        bindingDialog.root.layoutParams.width = (73.889f * w).toInt()
        bindingDialog.root.layoutParams.height = (34.556f * w).toInt()
        mIsShowLoading = true
    }

    override fun showLoading(cancelable: Boolean) {
        Handler(Looper.getMainLooper()).post {
            if (loadingDialog != null && mIsShowLoading) {
                loadingDialog?.cancel()
                loadingDialog = null
            }
            initDialog(cancelable)
        }
    }

    override fun hideLoading() {
        //cho chắc :(
        Handler(Looper.getMainLooper()).post {
            if (loadingDialog != null && mIsShowLoading && !isFinishing) {
                loadingDialog?.cancel()
                loadingDialog = null
            }
            mIsShowLoading = false
        }
    }
}