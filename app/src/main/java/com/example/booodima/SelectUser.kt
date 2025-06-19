package com.example.booodima

import android.content.Intent
import android.os.Bundle
import android.animation.ObjectAnimator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SelectUser : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.interface_select_user)

        val buttonStudent = findViewById<Button>(R.id.student_button)
        val buttonBoardingOwner = findViewById<Button>(R.id.boardingOwner_button)


     //   applyBlinkingEffect(buttonStudent)
     //   applyBlinkingEffect(buttonBoardingOwner)

        buttonStudent.setOnClickListener {
            val gotoNextPage = Intent(this, ChooseUniversity::class.java)
            startActivity(gotoNextPage)
        }

        buttonBoardingOwner.setOnClickListener {
            val gotoNextPage = Intent(this, SignIn::class.java)
            startActivity(gotoNextPage)
        }
    }

    // Function to apply blinking effect
    private fun applyBlinkingEffect(button: Button) {
        val blink = ObjectAnimator.ofFloat(button, "alpha", 1f, 0f)
        blink.duration = 1500
        blink.repeatCount = ObjectAnimator.INFINITE
        blink.repeatMode = ObjectAnimator.REVERSE
        blink.start()
    }
}
