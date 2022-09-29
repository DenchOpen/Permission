package com.dench.android.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dench.android.permission.core.PermissionManager
import com.dench.android.permission.databinding.ActivityMainBinding
import com.dench.android.permission.utils.ToastUtil

class MainActivity : AppCompatActivity(),
    PermissionManager.OnPermissionResultCallback {

    companion object {
        private const val TAG = "MainActivity"
        private const val REQUEST_CODE_FOR_PERMISSION_SETTINGS = 0x0101
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.buttonFirst.setOnClickListener {
            if (PermissionManager.hasPermissions(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                )
            ) {
                // after permission
                afterPermission()
            } else {
                showRequestPermissionDialog(
                    "申请设备读取权限的原因是因为我需要",
                    Manifest.permission.READ_PHONE_STATE
                )
            }

        }
        binding.buttonSecond.setOnClickListener {
            if (PermissionManager.hasPermissions(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // after permission
                afterPermission()
            } else {
                showRequestPermissionDialog(
                    "申请存储权限的原因是因为我需要",
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun afterPermission() {
        Log.d(TAG, "afterPermission: ")
        if (PermissionManager.hasPermissions(
                this,
                Manifest.permission.READ_PHONE_STATE
            )
        ) {
            ToastUtil.showToast(this, "已经获取设备读取权限")
        }

        if (PermissionManager.hasPermissions(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            ToastUtil.showToast(this, "已经获取Sdcard权限")
        }
    }

    private fun showRequestPermissionDialog(message: String, vararg perm: String) {
        AlertDialog.Builder(this)
            .setTitle("权限申请")
            .setMessage(message)
            .setPositiveButton("去授权") { dialog, which ->

                dialog.dismiss()
                PermissionManager.requestPermissions(this, *perm)
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
                ToastUtil.showToast(this, "已取消授权...")

            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.onRequestPermissionsResult(
            this,
            requestCode,
            permissions,
            grantResults,
            this
        )
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String?>) {
        ToastUtil.showToast(this, "授权成功")
        afterPermission()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String?>) {
        ToastUtil.showToast(this, "授权失败")
    }

    override fun onPermissionDeniedForever(requestCode: Int, perms: List<String?>) {
        showSettingsDialog()
    }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("权限申请")
            .setMessage("已永久拒绝，需要去设置->权限管理打开")
            .setPositiveButton("前往设置") { dialog, which ->
                dialog.dismiss()
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.fromParts("package", packageName, null))
                startActivityForResult(intent, REQUEST_CODE_FOR_PERMISSION_SETTINGS)
            }
            .setNegativeButton("取消") { dialog, which ->
                dialog.dismiss()
                ToastUtil.showToast(this, "已取消授权...")
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FOR_PERMISSION_SETTINGS) {
            afterPermission()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}