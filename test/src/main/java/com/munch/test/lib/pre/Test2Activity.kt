package com.munch.test.lib.pre

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.munch.lib.fast.base.activity.BaseTopActivity
import com.munch.pre.lib.extend.log
import com.munch.pre.lib.extend.setOnClickStart
import com.munch.test.lib.pre.databinding.ActivityTest2Binding
import kotlinx.coroutines.launch

/**
 * Create by munch1182 on 2021/4/7 14:34.
 */
class Test2Activity : BaseTopActivity() {

    private val bind by bind<ActivityTest2Binding>(R.layout.activity_test2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.lifecycleOwner = this
        lifecycleScope.launch {
            bind.test2Bt1.setOnClickStart {
                log(1)
            }
        }
    }
}