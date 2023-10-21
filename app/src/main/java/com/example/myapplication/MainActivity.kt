package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.transition.Explode
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.vnpay.extension.extensions.VNPRecycleviewExt.loadMore
import com.vnpay.extension.extensions.toJson
import com.vnpay.extension.launch.VnpayLaunch


class MainActivity : AppCompatActivity() {
    val adapterMain by lazy { AdapterMain() }
    val recycleview by lazy { findViewById<RecyclerView>(R.id.recycleview) }
    val motionLayout: MotionLayout by lazy { findViewById(R.id.parent) }
    val ivBack: View by lazy { findViewById(R.id.ivBack) }
    val search: SearchView by lazy { findViewById(R.id.search) }
    var isloadMore = false
    var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val vnpayLaunch = VnpayLaunch(this, lifecycle)
        vnpayLaunch.registerPermission()
        vnpayLaunch.register()
        with(window) {
            requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
            exitTransition = Explode()
        }
        setContentView(R.layout.activity_main)
        updateList()
        recycleview.adapter = adapterMain

        search.setOnClickListener {
            motionLayout.setTransition(R.id.start,R.id.end2)
            motionLayout.setTransitionDuration(500)
            motionLayout.transitionToEnd()
        }
        ivBack.setOnClickListener {
            motionLayout.setTransitionDuration(500)
            motionLayout.setTransition(R.id.end2,R.id.start)
            motionLayout.transitionToStart()
            motionLayout.setTransition(R.id.start,R.id.end)
        }
    }

    private fun loadMore() {
//        recycleview.loadMore(
//            loadMoreFirst = {
//                if (!isloadMore) {
//                    isloadMore = true
//                    insertFirst()
//                }
//            },
//            loadMoreLast = {
//                if (!isloadMore) {
//                    isloadMore = true
//                    insertLast()
//                }
//            })
    }

    private fun insertLast() {
        recycleview.postDelayed({
            if(adapterMain.isEmptyCacheLast())
                adapterMain.insertList(getData())
            else
                adapterMain.insertLastCache()
            isloadMore = false
        }, 1)
    }

    private fun insertFirst() {
        recycleview.postDelayed({
            adapterMain.insertFirst()
            isloadMore = false
        }, 1000)
    }

    private fun updateList() {
        recycleview.postDelayed({
            adapterMain.updateList(getData())
            isloadMore = false
        }, 1)
    }

    fun getData(): MutableList<ModelImage> {
        val list: MutableList<ModelImage> = mutableListOf()
        for (i in index..index+10) {
            list.add(
                ModelImage(
                    R.drawable.hinh3,
                    R.drawable.hinh3.toString(),
                    "http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4",
                    i.toString()
                ),
            )
        }
        index += list.size
        return list
    }
}