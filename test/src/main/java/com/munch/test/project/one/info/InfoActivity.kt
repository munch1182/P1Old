package com.munch.test.project.one.info

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.munch.lib.fast.BuildConfig
import com.munch.lib.fast.extend.load
import com.munch.pre.lib.base.BaseApp
import com.munch.pre.lib.extend.StringHelper
import com.munch.pre.lib.helper.AppHelper
import com.munch.test.project.one.R
import com.munch.test.project.one.base.BaseTopActivity
import com.munch.test.project.one.databinding.ActivityInfoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Create by munch1182 on 2021/3/31 16:17.
 */
class InfoActivity : BaseTopActivity() {

    private val bind by bind<ActivityInfoBinding>(R.layout.activity_info)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.lifecycleOwner = this
        val sb = StringBuilder()
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.Default) {
                AppHelper.PARAMETER.collect()
            }.forEach {
                sb.append(it.first).append(": ").append(it.second).append(StringHelper.LINE_SEPARATOR)
            }
            val vn = AppHelper.getVersionCodeAndName()
            sb.append("app version: ")
                .append("${vn?.second}${if (BaseApp.debug()) "-dev" else ""}(${vn?.first})(${BuildConfig.VERSION_CODE_4_DEBUG})")
            sb.append(StringHelper.LINE_SEPARATOR)
            //build后有效
            sb.append("build time: ").append(BuildConfig.BUILD_TIME)
            bind.infoPm.text = sb.toString()
            bind.infoIcon.load(
                AppHelper.getAppIcon(
                    this@InfoActivity,
                    this@InfoActivity.packageName
                )
            )
            val apps = AppHelper.getInstallApp(this@InfoActivity, this@InfoActivity.packageName)
            if (!apps.isNullOrEmpty()) {
                bind.infoIconShow.load(apps[0].loadIcon(packageManager))
            }
        }
    }

}