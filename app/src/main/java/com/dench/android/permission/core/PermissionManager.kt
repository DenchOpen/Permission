package com.dench.android.permission.core

import android.app.Activity
import androidx.annotation.Size

/**
 * 权限申请库-接口类
 */
object PermissionManager {

    const val DEFAULT_PERMISSION_REQUEST_CODE = 1002

    interface OnPermissionResultCallback {
        /**
         * 授权
         */
        fun onPermissionsGranted(requestCode: Int, perms: List<String?>)

        /**
         * 拒接
         */
        fun onPermissionsDenied(requestCode: Int, perms: List<String?>)

        /**
         * 永久拒绝,需要弹框阐述申请权限的原因
         */
        fun onPermissionDeniedForever(requestCode: Int, perms: List<String?>)
    }

    @JvmStatic
    fun hasPermissions(activity: Activity, @Size(min = 1) vararg perms: String): Boolean {
        return PermissionHelper.hasPermissions(activity, *perms)
    }

    @JvmStatic
    fun requestPermissions(
        activity: Activity,
        @Size(min = 1) vararg perms: String
    ) {
        requestPermissions(activity, DEFAULT_PERMISSION_REQUEST_CODE, *perms)
    }

    @JvmStatic
    fun requestPermissions(
        activity: Activity,
        requestCode: Int,
        @Size(min = 1) vararg perms: String
    ) {
        PermissionHelper.requestPermissions(activity, *perms, requestCode = requestCode)
    }

    @JvmStatic
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        callback: OnPermissionResultCallback
    ) {
        PermissionHelper.onRequestPermissionsResult(
            activity,
            requestCode,
            permissions,
            grantResults,
            callback
        )
    }

}