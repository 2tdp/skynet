package com.nmh.base_lib.utils

import android.os.SystemClock
import android.view.View

class UnDoubleClick(
    private var defaultInterval: Int = 840,
    private val onUnDoubleCLick: (View) -> Unit
) : View.OnClickListener {

    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) return

        lastTimeClicked = SystemClock.elapsedRealtime()
        onUnDoubleCLick(v)
    }
}