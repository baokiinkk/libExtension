package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.vnpay.extension.VNPOnSwipeTouchListener
import com.vnpay.extension.extensions.VNPBitmapExt
import com.vnpay.extension.launch.VnpayLaunch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vnpayLaunch = VnpayLaunch(this, lifecycle)
        vnpayLaunch.registerPermission()
        vnpayLaunch.register()
        setContentView(R.layout.activity_main)
        val parent = findViewById<ConstraintLayout>(R.id.parent)
        val button = findViewById<Button>(R.id.button)
        val text = findViewById<EditText>(R.id.text)

        parent.setOnTouchListener(object : VNPOnSwipeTouchListener(this){
            override fun onSwipeRight() {
                Log.d("quocbao","onSwipeRight")
            }

            override fun onSwipeLeft() {
                Log.d("quocbao","onSwipeLeft")
            }

            override fun onSwipeTop() {
                Log.d("quocbao","onSwipeTop")

            }

            override fun onSwipeBottom() {
                Log.d("quocbao","onSwipeBottom")

            }

        })
    }
}