package com.example.cognita.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cognita.R
import com.example.cognita.dashboard.DashboardState
import com.example.cognita.dashboard.DashboardViewModel
import com.example.cognita.exam.ActiveExamActivity
import com.example.cognita.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        setupClickListeners()
        setupObservers()
        setupBottomNavigation()

        // Fetch user stats
        val sharedPrefs = getSharedPreferences("CognitaPrefs", Context.MODE_PRIVATE)
        val email = sharedPrefs.getString("USER_EMAIL", "") ?: ""

        if (email.isEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        viewModel.loadStats(email)
    }

    private fun setupClickListeners() {
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish() // Returns to Dashboard
        }

        findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            // Clear preferences
            val sharedPrefs = getSharedPreferences("CognitaPrefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().remove("USER_EMAIL").apply()

            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Redirect to Login
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupObservers() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is DashboardState.Loading -> {
                    // Fetching data
                }
                is DashboardState.Success -> {
                    val data = state.stats
                    
                    findViewById<TextView>(R.id.tvProfileName).text = data.fullName ?: data.name
                    val sharedPrefs = getSharedPreferences("CognitaPrefs", Context.MODE_PRIVATE)
                    findViewById<TextView>(R.id.tvProfileEmail).text = sharedPrefs.getString("USER_EMAIL", "")
                    
                    findViewById<TextView>(R.id.tvProfileRank).text = data.rank.uppercase()
                    findViewById<TextView>(R.id.tvProfileSp).text = data.sp.toString()
                    findViewById<TextView>(R.id.tvProfileStreak).text = "${data.streak} Days"
                    findViewById<TextView>(R.id.tvProfileExams).text = (data.totalExamsTaken ?: 0).toString()
                    findViewById<TextView>(R.id.tvProfileAccuracy).text = "${data.averageScore ?: 0}%"
                }
                is DashboardState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_profile

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    finish() // Returns to dashboard
                    true
                }

                R.id.nav_exams -> {
                    startActivity(Intent(this, ActiveExamActivity::class.java))
                    finish() // Close profile to keep stack clear
                    true
                }

                R.id.nav_profile -> true // Already on profile
                
                else -> false
            }
        }
    }
}
