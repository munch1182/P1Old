package com.munch.project.one.permissions

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.munch.lib.dialog.withHandler
import com.munch.lib.fast.base.BaseBigTextTitleActivity
import com.munch.lib.fast.recyclerview.SimpleDiffAdapter
import com.munch.lib.fast.recyclerview.setOnItemClickListener
import com.munch.lib.helper.PhoneHelper
import com.munch.lib.recyclerview.BaseViewHolder
import com.munch.lib.recyclerview.OnItemClickListener
import com.munch.lib.result.ResultHelper
import com.munch.project.one.R
import com.munch.project.one.databinding.ActivityPermissionsBinding
import com.munch.project.one.databinding.ItemPermissionBinding

/**
 * Create by munch1182 on 2021/10/13 14:29.
 */
class PermissionActivity : BaseBigTextTitleActivity() {

    private val bind by bind<ActivityPermissionsBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind.permissionVersion.text = String.format(
            "android%s, version:%s",
            PhoneHelper.getSDKRelease().toString(),
            Build.VERSION.SDK_INT.toString()
        )

        val pa = SimpleDiffAdapter<PermissionBean, ItemPermissionBinding>(
            R.layout.item_permission, PermissionBeanDiffUtil()
        ) { _, db, bean -> db.permission = bean }
        bind.permissionRv.apply {
            layoutManager = LinearLayoutManager(this@PermissionActivity)
            adapter = pa
        }
        pa.setOnItemClickListener { _, pos, _ ->
            val pb = pa.data[pos] ?: return@setOnItemClickListener
            AlertDialog.Builder(this)
                .setTitle(pb.name.replace("android.permission.", ""))
                .setMessage(pb.note)
                .setNegativeButton(android.R.string.cancel) { dialog, _ -> dialog.cancel() }
                .show()
        }
        pa.setOnViewClickListener(OnPermissionClickListener(pa), R.id.permission_request)

        pa.set(Permissions.PERMISSIONS.toMutableList())
    }

    class PermissionBeanDiffUtil : DiffUtil.ItemCallback<PermissionBean>() {
        override fun areItemsTheSame(
            oldItem: PermissionBean,
            newItem: PermissionBean
        ): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: PermissionBean,
            newItem: PermissionBean
        ): Boolean {
            return oldItem.isGranted == newItem.isGranted
        }
    }

    inner class OnPermissionClickListener(private val pa: SimpleDiffAdapter<PermissionBean, ItemPermissionBinding>) :
        OnItemClickListener {

        private fun ResultHelper.CheckOrIntentResult.dialog(name: String): ResultHelper.CheckOrIntentResult {
            return this.explainIntent {
                AlertDialog.Builder(it)
                    .setMessage("请点击确定前往权限界面授予${name}权限")
                    .withHandler()
            }
                .explainAfterBackFromIntent {
                    AlertDialog.Builder(it)
                        .setMessage("$name 未成功获得，请点击确定前往权限界面重新授予该权限")
                        .withHandler()
                }
        }

        private fun ResultHelper.PermissionResult.dialog(): ResultHelper.PermissionResult {
            return explainPermission { context, permissions ->
                AlertDialog.Builder(context)
                    .setMessage(permissions.joinToString())
                    .withHandler()
            }.explainWhenDeniedPermanent { context, permissions ->
                AlertDialog.Builder(context)
                    .setMessage("前往设置界面授予${permissions.joinToString()}权限")
                    .withHandler()
            }
        }

        override fun onClick(v: View?, pos: Int, holder: BaseViewHolder) {
            super.onClick(v, pos, holder)
            val pb = pa.data[pos] ?: return
            val name = pb.name
            val start = pb.isGranted
            val intent = pb.intent
            val resultHandle: (Boolean) -> Unit = {
                if (!it) {
                    toast("${name}获取失败")
                }
                if (it && pb.isGranted != start) {
                    pa.notifyItemChanged(pos)
                }
            }
            if (intent != null) {
                ResultHelper.init(this@PermissionActivity)
                    .with(pb.isGrantedJudge, intent)
                    .dialog(name)
                    .start(resultHandle)
            } else {
                ResultHelper.init(this@PermissionActivity)
                    .with(name)
                    .dialog()
                    .request(resultHandle)
            }
        }

    }
}