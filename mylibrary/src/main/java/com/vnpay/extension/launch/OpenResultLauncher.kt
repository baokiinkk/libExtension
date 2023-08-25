package com.vnpay.extension.launch

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts

class OpenResultLauncher : IActivityResultLauncher<Intent, ActivityResult>() {

    override fun getActivityContract(): ActivityResultContracts.StartActivityForResult {
        return ActivityResultContracts.StartActivityForResult()
    }
}

class OpenPermissionResultLauncher :
    IActivityResultLauncher<Array<String>, Map<String, Boolean>>() {
    override fun getActivityContract(): ActivityResultContracts.RequestMultiplePermissions {
        return ActivityResultContracts.RequestMultiplePermissions()
    }
}