package com.munch.project.testsimple

import android.content.Intent
import android.view.View
import com.munch.lib.helper.isServiceRunning
import com.munch.lib.helper.startActivity
import com.munch.lib.log
import com.munch.lib.test.recyclerview.TestRvActivity
import com.munch.lib.test.recyclerview.TestRvItemBean

/**
 * Create by munch1182 on 2020/12/16 17:09.
 */
class TestFunActivity : TestRvActivity() {

    override fun setupIntent() {
        super.setupIntent()
        intent = Intent().putExtras(newBundle("Test Function", null, isBtn = true))
    }

    override fun clickItem(view: View, pos: Int) {
        super.clickItem(view, pos)
        when (pos) {
            0 -> {
            }
            1 -> {
            }
            2 -> {
            }
            3 -> {
            }
            4 -> {
            }
            5 -> {
                val running = isServiceRunning()

                val str = when {
                    running == null -> {
                        toast("错误")
                        return
                    }
                    running -> {
                        "有"
                    }
                    else -> {
                        "没有"
                    }
                }
                toast(str.plus("服务运行中"))
            }
            6 ->startActivity(TestFunInFragmentActivity::class.java)
        }
    }

    override fun getItems(): MutableList<TestRvItemBean> {
        return TestRvItemBean.newArray(
            "test1",
            "test2",
            "test3",
            "test4",
            "test5",
            "test6",
            "testInFragment"
        )
    }
}