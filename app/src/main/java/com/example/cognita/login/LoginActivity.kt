package com.example.cognita.login


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cognita.R
import com.example.cognita.dashboard.DashboardActivity
import com.example.cognita.register.RegisterActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        // NEW: Link the register button
        val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)

        // Observe the ViewModel state
        viewModel.loginState.observe(this, Observer { state ->
            when (state) {
                is LoginState.Loading -> {
                    btnLogin.isEnabled = false
                    btnLogin.text = "LOGGING IN..."
                }
                is LoginState.Success -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = "LOGIN"

                    // Save user email in SharedPreferences for session tracking
                    val email = etEmail.text.toString().trim()
                    val sharedPrefs = getSharedPreferences("CognitaPrefs", Context.MODE_PRIVATE)
                    sharedPrefs.edit().putString("USER_EMAIL", email).apply()

                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()

                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is LoginState.Error -> {
                    btnLogin.isEnabled = true
                    btnLogin.text = "LOGIN"
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                }
                is LoginState.Idle -> {
                    // Do nothing
                }
            }
        })

        // Login Button Click
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            viewModel.login(email, password)
        }

        // NEW: Register Button Click
        btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("CognitaPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("JWT_TOKEN", token).apply()
    }
}