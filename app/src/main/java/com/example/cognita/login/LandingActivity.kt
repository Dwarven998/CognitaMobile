package com.example.cognita

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.cognita.login.LoginActivity
import com.example.cognita.register.RegisterActivity

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGetStarted = findViewById<Button>(R.id.btnGetStarted)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnGetStarted.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}