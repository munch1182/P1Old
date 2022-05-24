package com.munch.lib.result

import android.app.Activity
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.munch.lib.log.Logger

/**
 * Created by munch1182 on 2022/4/10 3:58.
 */
class ResultFragment : Fragment() {

    private var result: OnIntentResultListener? = null
    private var permission: OnPermissionResultListener? = null
    private val log = Logger("result")

    val isValid: Boolean
        get() = activity != null && !requireActivity().isDestroyed && !requireActivity().isFinishing

    private val permissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            log.log {
                val str = it.map { p -> "${p.key}: ${p.value}" }.joinToString()
                "MultiplePermission result: $str"
            }
            permission?.onPermissionResult(!it.any { v -> !v.value }, it)
            permission = null
        }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            log.log {
                val r = when (it.resultCode) {
                    Activity.RESULT_OK -> "Ok"
                    Activity.RESULT_CANCELED -> "Cancel"
                    else -> it.resultCode.toString()
                }
                "Intent result: $r"
            }
            result?.onIntentResult(it.resultCode == Activity.RESULT_OK, it.data)
            result = null
        }


    fun startIntent(intent: Intent, listener: OnIntentResultListener?) {
        if (result != null) {
            throw IllegalStateException("result not null.")
        }
        log.log { "start intent: ${intent}." }
        result = listener
        resultLauncher.launch(intent)
    }

    fun requestPermissions(permissions: Array<String>, listener: OnPermissionResultListener?) {
        if (permission != null) {
            throw IllegalStateException("permission not null.")
        }
        log.log { "start permission: ${permissions.joinToString()}." }
        permission = listener
        permissionsLauncher.launch(permissions)
    }

}