# Android 动态权限申请

#### 0x01 介绍

由于 Android 动态权限申请是一个交互比较复杂的模块，整个申请的流程也比较长，所以，写了一个工具来封装了一个，也具体的实现了一个流程。

由于每个App的Ui风格不一致，所以没有把Toast和弹框封装进工具，等后期有好的想法再优化。

#### 0x02 动态权限申请流程

1. 检查授权状态
2. 申请权限
3. 处理权限申请结果
4. 当用户永久拒绝，引导去手机设置
5. 检查手机设置后的权限申请结果

#### 0x03 使用说明

主要封装类 `PermissionManager`

根据业务需要用到安卓定义的高危权限，需要去动态申请权权限。通常是在Activity 和 Fragment 组件中发起。

1. 检查权限授权状态

```kotlin
if (PermissionManager.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
    // after permission
    afterPermission()
} else {
    showRequestPermissionDialog("申请手机权限的原因是因为我需要", Manifest.permission.READ_PHONE_STATE)
}
```

2. 申请权限，根据合规化通常需要先弹框

```kotlin
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
```

3. 处理权限申请结果

在 `onRequestPermissionsResult` 回调接口中，调用 `PermissionManager.onRequestPermissionsResult` 方法，并且实现 `PermissionManager.OnPermissionResultCallback` 这个回调接口。

```kotlin
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
```

1. 当用户永久拒绝，引导去手机设置

```kotlin
    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("权限申请")
            .setMessage("已永久拒绝，需要去设置->权限设置打开")
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
```

5. 检查手机设置后的权限申请结果

```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_FOR_PERMISSION_SETTINGS) {
            afterPermission()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
```
