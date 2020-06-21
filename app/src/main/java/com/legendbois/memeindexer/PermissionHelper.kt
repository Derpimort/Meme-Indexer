package com.legendbois.memeindexer

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions

object PermissionHelper {
    const val PERMISSION_CODE = 11
    private val PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    fun hasPermissions(activity: Activity): Boolean = PERMISSIONS.all {
        ActivityCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
    }

    fun getPermissions(activity: Activity) {
        Log.d("MemeIndexer", "Requesting permissions")
        requestPermissions(activity, PERMISSIONS, PERMISSION_CODE)

    }
}