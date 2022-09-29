package com.dench.android.permission.core

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.Size
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 *
 * @Description 权限检查
 * @Author:   huyl
 * @date:  2021/4/7 11:23 AM
 */

object PermissionHelper {

    /**
     * 检查权限
     */
    @JvmStatic
    fun hasPermissions(activity: Activity, @Size(min = 1) vararg perms: String): Boolean {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(
                    activity,
                    perm
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    @JvmStatic
    fun requestPermissions(
        activity: Activity,
        vararg permissionStorage: String,
        requestCode: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                activity,
                permissionStorage,
                requestCode
            )
        }
    }

    @JvmStatic
    fun onRequestPermissionsResult(
        activity: Activity,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        callback: PermissionManager.OnPermissionResultCallback
    ) {
        val granted: MutableList<String> = ArrayList()
        val denied: MutableList<String> = ArrayList()
        val forever: MutableList<String> = ArrayList()
        permissions.forEachIndexed { i, perm ->
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm)
            } else {
                if (!shouldShowRationale(activity, perm)) {
                    forever.add(perm)
                } else {
                    denied.add(perm)
                }
            }
        }

        // Report granted permissions, if any.
        if (granted.isNotEmpty()) {
            callback.onPermissionsGranted(requestCode, granted)
        }

        // Report denied permissions, if any.
        if (denied.isNotEmpty()) {
            callback.onPermissionsDenied(requestCode, denied)
        }

        if (forever.isNotEmpty()) {
            callback.onPermissionDeniedForever(requestCode, forever)
        }
    }


    private fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }
}