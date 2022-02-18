package com.munch.lib.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import com.munch.lib.helper.ARSHelper
import java.lang.ref.WeakReference

/**
 * Create by munch1182 on 2021/10/28 15:14.
 */
object AppForegroundHelper : ARSHelper<OnAppForegroundChangeListener> {

    fun isScreenOn(context: Context = AppHelper.app): Boolean? {
        val pw = context.getSystemService(Context.POWER_SERVICE) as? PowerManager?
            ?: return null
        return pw.isInteractive
    }

    private var resumeActivity: WeakReference<Activity>? = null
    private var activityIndex = 0
        set(value) {
            field = value
            notifyListener { it.invoke(isInForeground) }
        }
    val isInForeground: Boolean
        //有activity回调了Resumed但是没有回调Paused即处于前台
        get() = activityIndex == 1
    val currentActivity: Activity?
        get() = resumeActivity?.get()

    private val cb by lazy {
        object : Application.ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                activityIndex++
                resumeActivity = WeakReference(activity)
            }

            override fun onActivityPaused(activity: Activity) {
                activityIndex--
                resumeActivity?.clear()
                resumeActivity = null
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        }
    }

    fun register(app: Application) {
        app.registerActivityLifecycleCallbacks(cb)
    }

    fun unregister(app: Application) {
        app.unregisterActivityLifecycleCallbacks(cb)
    }

    private val changeListeners = mutableListOf<OnAppForegroundChangeListener>()

    override val arrays: MutableList<OnAppForegroundChangeListener>
        get() = changeListeners
}

typealias OnAppForegroundChangeListener = (isInForeground: Boolean) -> Unit