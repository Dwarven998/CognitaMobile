package com.example.cognita.dashboard


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cognita.R
import com.example.cognita.exam.ActiveExamActivity

class DashboardActivity : AppCompatActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvRank = findViewById<TextView>(R.id.tvRank)
        val tvSp = findViewById<TextView>(R.id.tvSp)
        val btnBlitz = findViewById<Button>(R.id.btnBlitz)
        val btnMock = findViewById<Button>(R.id.btnMock)

        val prefs = getSharedPreferences("CognitaPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("JWT_TOKEN", "") ?: ""

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is DashboardState.Success -> {
                    tvRank.text = state.stats.rank
                    tvSp.text = "${state.stats.sp} SP"
                }
                is DashboardState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is DashboardState.Loading -> {
                    tvRank.text = "Loading..."
                }
            }
        })

        if (token.isNotEmpty()) viewModel.loadStats(token)

        btnBlitz.setOnClickListener { launchExam("BLITZ") }
        btnMock.setOnClickListener { launchExam("MOCK") }
    }

    private fun launchExam(mode: String) {
        val intent = Intent(this, ActiveExamActivity::class.java)
        intent.putExtra("EXAM_MODE", mode)
        startActivity(intent)
    }
}