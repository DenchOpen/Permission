package com.dench.android.permission.utils

import android.content.Context
import android.widget.Toast

object ToastUtil {

    @JvmStatic
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}