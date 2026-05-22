package com.example.cognita.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cognita.R
import com.example.cognita.exam.ActiveExamActivity
import com.example.cognita.goal.GoalActivity
import com.example.cognita.results.ResultsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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

        // TODO: Replace with real user ID logic from your auth session / SharedPreferences
        val userId = intent.getStringExtra("USER_ID") ?: "current_user_id"

        // Trigger the backend fetch
        // viewModel.fetchLiveDashboardData(userId)
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
    }

    private fun setupObservers() {
        // Observe LiveData from your DashboardViewModel to populate real data dynamically
        /*
        viewModel.userData.observe(this) { data ->
            findViewById<android.widget.TextView>(R.id.tvUserName).text = data.fullName ?: "Student!"
            findViewById<android.widget.TextView>(R.id.tvExamsCount).text = data.totalExamsTaken?.toString() ?: "0"
            findViewById<android.widget.TextView>(R.id.tvAvgScore).text = "${data.averageScore ?: 0}%"
        }

        viewModel.errorMessage.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
        */
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
                    // TODO: Route to your actual ProfileActivity once created
                    Toast.makeText(this, "Profile module coming soon", Toast.LENGTH_SHORT).show()
                    true
                }

                else -> false
            }
        }
    }
}