package com.example.booodima

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class InitialInterfaceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.initial_interface)

        val startButton = findViewById<Button>(R.id.startButton)
        val imageView: ImageView = findViewById(R.id.imageView)

        val fadeInTitle1 = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f)

        val title1Set = AnimatorSet().apply {
            playTogether(fadeInTitle1)
            duration = 1000
        }

        AnimatorSet().apply {
            playSequentially(title1Set)
            start()
        }

        startButton.setOnClickListener{
            val gotoNextPage = Intent(applicationContext, SelectUser::class.java)
            startActivity(gotoNextPage)
        }
    }
}

