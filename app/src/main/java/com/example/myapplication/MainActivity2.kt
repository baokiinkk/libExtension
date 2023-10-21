package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import android.transition.TransitionInflater
import android.widget.VideoView
import com.vnpay.extension.extensions.fromJson

class MainActivity2 : AppCompatActivity() {
    val model:ModelImage? by lazy {
        intent.getStringExtra("TEXT_IMAGE")?.fromJson()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        window.sharedElementEnterTransition =
            TransitionInflater.from(this).inflateTransition(R.transition.shared_element_transition);
        val image = findViewById<ImageView>(R.id.imageView)
        model?.let {
            image.setImageResource(it.image)
            image.transitionName = it.transaction
        }
    }
}