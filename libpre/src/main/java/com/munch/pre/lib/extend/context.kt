package com.munch.pre.lib.extend

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.munch.pre.lib.helper.AppHelper

/**
 * Create by munch1182 on 2021/3/31 11:33.
 */
inline fun <reified T> Context.getService(name: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.getSystemService(T::class.java)
    } else {
        this.getSystemService(name) as? T
    }
}

fun Context.startActivity(target: Class<out Activity>) {
    startActivity(Intent(this, target))
}

fun Context.startActivity(target: Class<out Activity>, bundle: Bundle) {
    startActivity(Intent(this, target).apply {
        putExtras(bundle)
    })
}


fun Context.restartApp2Activity(clazz: Class<out Activity>, bundle: Bundle? = null) {
    AppHelper.resetApp2Activity(this, clazz, bundle)
}

fun Context.restartApp2Activity(bundle: Bundle? = null) {
    AppHelper.resetApp2Activity(this, bundle)
}

inline fun Context.startActivity(clazz: Class<out Activity>, func: Bundle.() -> Unit) {
    val empty = Bundle()
    func.invoke(empty)
    startActivity(clazz, empty)
}

fun Context.startServiceInForeground(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

fun Context.getBackIcon(): Drawable? {
    val a = obtainStyledAttributes(intArrayOf(android.R.attr.homeAsUpIndicator))
    val result = a.getDrawable(0)
    a.recycle()
    return result
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Context.getBackIconWhite(): Drawable? {
    return getBackIcon()?.apply {
        DrawableCompat.setTint(this, Color.WHITE)
    }
}

fun Context.getListDivider(): Drawable? {
    val a = obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
    val divider = a.getDrawable(0)
    a.recycle()
    return divider
}

fun Context.dp2Px(dpVal: Float): Float {
    return dpVal * this.resources.displayMetrics.density + 0.5f
}

fun Context.px2Dp(pxVal: Float): Float {
    return pxVal / this.resources.displayMetrics.density + 0.5f
}

fun Context.dp2Sp(dpVal: Float): Float {
    return dpVal * this.resources.displayMetrics.scaledDensity + 0.5f
}

fun Context.sp2Dp(pxVal: Float): Float {
    return pxVal / this.resources.displayMetrics.scaledDensity + 0.5f
}

/**
 * 虽然方法已废弃，但仍会返回自己的服务
 */
@Suppress("DEPRECATION")
fun Context.isServiceRunning(service: Class<out Service>? = null): Boolean? {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager? ?: return null
    val services = manager.getRunningServices(Int.MAX_VALUE)
    services.forEach {
        service ?: return true
        if (it.service.className == service.name) {
            return true
        }
    }
    return false
}

/**
 * 即使声明不同进程的服务也会被stop
 * 如果传入为空，将关闭所有应用下的服务
 */
@Suppress("DEPRECATION")
fun Context.stopServices(vararg service: Class<out Service>) {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager? ?: return
    val services = manager.getRunningServices(Int.MAX_VALUE)
    services.forEach service@{
        if (service.isEmpty()) {
            stopService(Intent(this, Class.forName(it.service.className)))
        } else {
            service.forEach services@{ clazz ->
                if (it.service.className == clazz.name) {
                    stopService(Intent(this, Class.forName(it.service.className)))
                    return@service
                }
            }
        }
    }
}

fun Context.getAttrFromTheme(attrId: Int): TypedValue {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrId, typedValue, true)
    return typedValue
}

fun Context.getAttrArrayFromTheme(attrId: Int, get: TypedArray.() -> Int): Int {
    val typedArray = theme.obtainStyledAttributes(intArrayOf(attrId))
    val value = get.invoke(typedArray)
    typedArray.recycle()
    return value
}

/**
 * 获取actionbar高度，无缓存，未获取到则为-1
 */
fun Context.getActionBarSize() = TypedValue.complexToDimensionPixelSize(
    getAttrFromTheme(android.R.attr.actionBarSize).data,
    resources.displayMetrics
)

@ColorInt
fun Context.getColorCompat(@ColorRes colorId: Int): Int {
    return ContextCompat.getColor(this, colorId)
}

fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable? {
    return ContextCompat.getDrawable(this, resId)
}
