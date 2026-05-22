package com.example.cognita.results


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cognita.R

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val score = intent.getIntExtra("SCORE", 0)
        val total = intent.getIntExtra("TOTAL", 0)
        val spGained = (score * 10) // Basic gamification math

        findViewById<TextView>(R.id.tvFinalScore).text = "$score / $total"
        findViewById<TextView>(R.id.tvSpGained).text = "+$spGained SP"

        val accuracy = if (total > 0) ((score.toFloat() / total) * 100).toInt() else 0
        findViewById<TextView>(R.id.tvAccuracy).text = "$accuracy%"

        findViewById<Button>(R.id.btnReturnHome).setOnClickListener {
            finish() // Drops the user back to the Dashboard naturally
        }
    }
}