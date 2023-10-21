package com.vnpay.extension.extensions

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import java.io.File

object VNPActivityExt {
    fun FragmentActivity.copyTextToClipboard(textToCopy: String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", textToCopy)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "copied", Toast.LENGTH_SHORT).show()
    }

    fun FragmentActivity.getTextFromClipboard(): String {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboardManager.primaryClip?.getItemAt(0)?.text.toString()
    }

    fun FragmentActivity.sendDataToOtherApps(data: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, data)
            type = "text/plain"
        }
        startActivity(sendIntent)
    }

    fun FragmentActivity.openPhoneNumber(phone: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phone")
        startActivity(intent)
    }

    fun FragmentActivity.openEmail(email: String, subject: String = "", body: String = "") {
        val intent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:${email}")
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        startActivity(Intent.createChooser(intent, "Email"))
    }

    fun FragmentActivity.openGoogleMap(address: String) {
        val uri = "https://www.google.com/maps/search/?api=1&query=$address"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    fun FragmentActivity.isPackageInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun FragmentActivity.openPlayStore(packageName: String) {
        val playStoreIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
        startActivity(playStoreIntent)
    }

    fun FragmentActivity.goToSettingLocation(settingRequestCode: Int) {
        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), settingRequestCode)
    }

    fun FragmentActivity.openFile(file: File?, type: String = "application/pdf") {
        val target = Intent(Intent.ACTION_VIEW)
        val uri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(file)
        } else {
            FileProvider.getUriForFile(
                this,
                this.applicationContext.packageName + ".provider",
                file ?: return
            )
        }
        target.setDataAndType(uri, type)
        target.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                Intent.FLAG_ACTIVITY_NO_HISTORY

        val intent = Intent.createChooser(target, "Open File")
        try {
            startActivity(intent)
        } catch (exception: ActivityNotFoundException) {
            exception.printStackTrace()
        }
    }

    fun FragmentActivity.getDeviceId() =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID).orEmpty()




    //authen:Thanh Nguyễn
    // flow : main ->A ->B -> C -> D ->A
    // output: onDestroy c ->onDestroy B->onDestroy A->onDestroy Main ->onCreate A ->onDestroy D
    fun Intent.popAndNewTask() {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    // flow : main ->A ->B -> C -> D -> A
    // output: onDestroy c ->onDestroy B ->onDestroy A->onCreate A -> onDestroy D
    fun Intent.popAndNewScreen() {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }

    // flow : main ->A ->B -> C -> D->A
    // output: main ->A ->B -> C -> D -> A  -> luôn luôn tạo mới 1 activity trên top của task
    fun Intent.startSingleTop() {
        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

    }

    // flow : main ->A ->B -> C -> D->
    // output: onDestroy C ->onDestroy B ->onNewIntent A -> onDestroy D
    fun Intent.popAndKeepScreen() {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

    }
}