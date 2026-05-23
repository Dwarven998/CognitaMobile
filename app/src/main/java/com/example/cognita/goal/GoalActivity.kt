package com.example.cognita.goal

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cognita.R
import com.example.cognita.dashboard.DashboardActivity

class GoalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val btnDost = findViewById<LinearLayout>(R.id.btnDostSei)

        btnDost.setOnClickListener {
            Toast.makeText(this, "DOST-SEI Context Activated", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}