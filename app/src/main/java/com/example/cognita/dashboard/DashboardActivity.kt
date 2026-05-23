package com.example.cognita.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cognita.R
import com.example.cognita.exam.ActiveExamActivity
import com.example.cognita.goal.GoalActivity
import com.example.cognita.results.ResultsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.cognita.login.LoginActivity
import com.example.cognita.profile.ProfileActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var viewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Reverted back to the standard setContentView approach
        setContentView(R.layout.activity_dashboard)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]

        setupClickListeners()
        setupObservers()
        setupBottomNavigation()

        // Retrieve stored email from SharedPreferences for dynamic session state
        val sharedPrefs = getSharedPreferences("CognitaPrefs", MODE_PRIVATE)
        val email = sharedPrefs.getString("USER_EMAIL", "") ?: ""

        if (email.isEmpty()) {
            // Redirect to Login if session does not exist
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // Trigger dynamic stats fetch from Spring Boot backend using email
        viewModel.loadStats(email)
    }

    private fun setupClickListeners() {
        // Main Action Cards using findViewById
        findViewById<View>(R.id.btnStartExam).setOnClickListener {
            startActivity(Intent(this, ActiveExamActivity::class.java))
        }

        findViewById<View>(R.id.btnViewResults).setOnClickListener {
            startActivity(Intent(this, ResultsActivity::class.java))
        }

        findViewById<View>(R.id.btnGoals).setOnClickListener {
            startActivity(Intent(this, GoalActivity::class.java))
        }

        // Top-right Profile Avatar Logout Action
        findViewById<View>(R.id.profileAvatar).setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menu.add("Profile")
            popup.menu.add("Logout")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Profile" -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }
                    "Logout" -> {
                        // Clear preferences
                        val sharedPrefs = getSharedPreferences("CognitaPrefs", MODE_PRIVATE)
                        sharedPrefs.edit().remove("USER_EMAIL").apply()
                        
                        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
                        
                        // Redirect to Login
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
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
                    findViewById<android.widget.TextView>(R.id.tvUserName).text = (data.fullName ?: data.name) + " 👋"
                    findViewById<android.widget.TextView>(R.id.tvExamsCount).text = (data.totalExamsTaken ?: 0).toString()
                    findViewById<android.widget.TextView>(R.id.tvAvgScore).text = "${data.averageScore ?: 0}%"
                }
                is DashboardState.Error -> {
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        // Use findViewById directly for the navigation bar
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_dashboard

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> true // Already on dashboard

                R.id.nav_exams -> {
                    startActivity(Intent(this, ActiveExamActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }
    }
}