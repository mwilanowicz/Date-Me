package com.example.date_me.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.date_me.MainActivity
import com.example.date_me.R
import com.example.date_me.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val user = FirebaseAuth.getInstance().currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if (user == null)
                startActivity(Intent(this, LoginActivity::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))
            finish()

        }, 2000)
    }
}