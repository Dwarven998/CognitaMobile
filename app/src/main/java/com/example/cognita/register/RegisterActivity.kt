package com.example.cognita.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cognita.R
import com.example.cognita.goal.GoalActivity
import com.example.cognita.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPass = findViewById<EditText>(R.id.etRegPassword)
        val etConfirm = findViewById<EditText>(R.id.etRegConfirm)
        val etFullName = findViewById<EditText>(R.id.etRegFullName)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoToLogin = findViewById<Button>(R.id.btnGoToLogin)

        // NEW: Back button
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // Register state observer
        viewModel.state.observe(this, Observer { state ->
            when (state) {

                is RegisterState.Loading -> {
                    btnRegister.isEnabled = false
                    btnRegister.text = "CREATING ACCOUNT..."
                }

                is RegisterState.Success -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = "CREATE ACCOUNT"

                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, GoalActivity::class.java))
                    finish()
                }

                is RegisterState.Error -> {
                    btnRegister.isEnabled = true
                    btnRegister.text = "CREATE ACCOUNT"

                    Log.e("CognitaAuth", "Registration Failed: ${state.message}")

                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }

                is RegisterState.Idle -> {
                    // Do nothing
                }
            }
        })

        // Register button click
        btnRegister.setOnClickListener {
            viewModel.register(
                etEmail.text.toString().trim(),
                etPass.text.toString().trim(),
                etConfirm.text.toString().trim(),
                etFullName.text.toString().trim()
            )
        }

        // NEW: Sign In button navigation
        btnGoToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // NEW: Back button navigation
        btnBack.setOnClickListener {
            finish()
        }
    }
}