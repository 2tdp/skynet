package com.nmh.base_lib.extensions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import com.nmh.base_lib.utils.UnDoubleClick

fun View.setOnUnDoubleClickListener(onUnDoubleClick: (View) -> Unit) {
    setOnClickListener(UnDoubleClick {
        onUnDoubleClick(it)
    })
}

fun View.setOnClickEffectListener(onClick: (View) -> Unit) {
    setOnClickListener {
        applyClickEffect(it)
        onClick(it)
    }
}

fun applyClickEffect(view: View) {
    val animation = AlphaAnimation(1.0f, 0.5f) .apply {
        duration = 134
        repeatMode = AlphaAnimation.REVERSE
        repeatCount = 1

    }
    view.startAnimation(animation)
}

fun View.loadBitmapFromView(): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    layout(left, top, right, bottom)
    draw(canvas)
    return bitmap
}

fun View.createBackground(colorArr: IntArray, border: Float, stroke: Int, colorStroke: Int) {
    background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = border
        if (stroke != -1) setStroke(stroke, colorStroke)

        if (colorArr.size >= 2) {
            colors = colorArr
            gradientType = GradientDrawable.LINEAR_GRADIENT
        } else setColor(colorArr[0])
    }
}

fun View.createBackground(colorArr: IntArray, border: FloatArray, stroke: Int, colorStroke: Int) {
    background = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadii = border
        if (stroke != -1) setStroke(stroke, colorStroke)

        if (colorArr.size >= 2) {
            colors = colorArr
            gradientType = GradientDrawable.LINEAR_GRADIENT
        } else setColor(colorArr[0])
    }
}

fun View.effectPressRectangle(): View {
    val value = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, value, true)
    setBackgroundResource(value.resourceId)
    isFocusable = true // Required for some view types
    return this
}

fun View.effectPressOval(): View {
    val outValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true)
    setBackgroundResource(outValue.resourceId)
    isFocusable = true // Required for some view types
    return this
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}