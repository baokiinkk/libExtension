package com.example.myapplication

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vnpay.extension.KeyboardVisibilityEventListener
import com.vnpay.extension.VnpayManager.getFormatMoneybyCcy
import com.vnpay.extension.VnpayManager.setEventListener
import com.vnpay.extension.extensions.toJson
import com.vnpay.extension.launch.VnpayLaunch

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vnpayLaunch = VnpayLaunch(this, lifecycle)
        vnpayLaunch.registerPermission()
        vnpayLaunch.register()
        setContentView(R.layout.activity_main2)
        setEventListener(object : KeyboardVisibilityEventListener {
            override fun onVisibilityChanged(isOpen: Boolean) {
                Log.d("quocbao", isOpen.toJson())
            }
        })
        val listPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        vnpayLaunch.launchPermission(
            arrayPermission = listPermission,
            callback = object : VnpayLaunch.PermissionsCallBack {
                override fun onSuccess() {
                    Log.d("quocbao", "SUCCESS")
                }

                override fun onDeny(lisDenyPermission: ArrayList<String>) {
                    Log.d("quocbao", lisDenyPermission.toJson())
                }

                override fun onFail(listFailPermission: ArrayList<String>) {
                    Log.d("quocbao", "FAIL:${listFailPermission.toJson()}")
                }

            }
        )
        findViewById<TextView>(R.id.editTextTextPersonName).text = "32462374,26,2748".getFormatMoneybyCcy()
    }
}