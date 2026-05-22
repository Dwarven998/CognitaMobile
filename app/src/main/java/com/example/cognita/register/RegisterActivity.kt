package com.example.cognita.register


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cognita.R
import com.example.cognita.goal.GoalActivity

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // 1. Initialize your views
        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPass = findViewById<EditText>(R.id.etRegPassword)
        val etConfirm = findViewById<EditText>(R.id.etRegConfirm)
        val etFullName = findViewById<EditText>(R.id.etRegFullName) // New Field
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        viewModel.state.observe(this, Observer { state ->
            when (state) {
                is RegisterState.Loading -> {
                    btnRegister.isEnabled = false
                    btnRegister.text = "CREATING ACCOUNT..."
                }
                // In RegisterActivity.kt, inside your observer:
                is RegisterState.Success -> {
                    btnRegister.isEnabled = true

                    // If you need the token later, you might need to fix the backend to return JSON.
                    // For now, this will allow the app to proceed without crashing.
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
                is RegisterState.Idle -> {}
            }
        })

        btnRegister.setOnClickListener {
            // 2. Pass the fullName to the ViewModel
            viewModel.register(
                etEmail.text.toString(),
                etPass.text.toString(),
                etConfirm.text.toString(),
                etFullName.text.toString() // Added here
            )
        }
    }
}