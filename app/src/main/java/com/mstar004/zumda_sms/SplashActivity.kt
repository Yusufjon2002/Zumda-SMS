package com.mstar004.zumda_sms

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        window?.statusBarColor = Color.TRANSPARENT

        val img = findViewById<ImageView>(R.id.splashScreenLogoImageView)
        val tv = findViewById<TextView>(R.id.splashScreenWelcomeMessageTextView)

        val anim = AnimationUtils.loadAnimation(this, R.anim.animation)
        val anim2 = AnimationUtils.loadAnimation(this, R.anim.animation)

        anim.reset()
        anim2.reset()

        img.startAnimation(anim2)
        tv.startAnimation(anim)
        Handler().postDelayed(Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3500)
    }
}