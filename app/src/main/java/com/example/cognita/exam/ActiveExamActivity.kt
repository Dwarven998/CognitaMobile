package com.example.cognita.exam

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.cognita.R
import com.example.cognita.results.ResultsActivity
import java.util.Locale

class ActiveExamActivity : AppCompatActivity() {

    private val viewModel: ExamViewModel by viewModels()
    private lateinit var mode: String
    private lateinit var token: String

    // Anti-cheat tracking
    private var strikes = 0
    private var isSubmitting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_exam)

        mode = intent.getStringExtra("EXAM_MODE") ?: "BLITZ"
        token = getSharedPreferences("CognitaPrefs", Context.MODE_PRIVATE).getString("JWT_TOKEN", "") ?: ""

        val tvQuestion = findViewById<TextView>(R.id.tvQuestion)
        val tvTrivia = findViewById<TextView>(R.id.tvTrivia)
        val btnA = findViewById<Button>(R.id.btnA)
        val btnB = findViewById<Button>(R.id.btnB)
        val btnC = findViewById<Button>(R.id.btnC)
        val btnD = findViewById<Button>(R.id.btnD)
        val btnNext = findViewById<Button>(R.id.btnNext)

        // 1. Observe Question Data
        viewModel.questions.observe(this, Observer { questions ->
            if (questions.isNotEmpty()) {
                displayQuestion(0)
            }
        })

        // 2. Observe Index Changes
        viewModel.currentQuestionIndex.observe(this, Observer { index ->
            displayQuestion(index)
        })

        // 3. Observe Submission State (Routing to Results)
        viewModel.submissionState.observe(this, Observer { state ->
            when (state) {
                is ExamSubmissionState.Submitting -> {
                    isSubmitting = true
                    btnNext.isEnabled = false
                    btnNext.text = "SUBMITTING..."
                }
                is ExamSubmissionState.Success -> {
                    val intent = Intent(this, ResultsActivity::class.java).apply {
                        putExtra("SCORE", state.score)
                        putExtra("TOTAL", state.total)
                    }
                    startActivity(intent)
                    finish() // Prevent going back
                }
                is ExamSubmissionState.Error -> {
                    isSubmitting = false
                    btnNext.isEnabled = true
                    btnNext.text = "RETRY SUBMIT"
                    Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show()
                }
                is ExamSubmissionState.Idle -> { }
            }
        })

        if (token.isNotEmpty()) {
            viewModel.loadExam(token, mode)
        }

        // Setup Option Clicks
        val optionButtons = listOf(btnA, btnB, btnC, btnD)
        optionButtons.forEachIndexed { optIndex, button ->
            button.setOnClickListener {
                if (isSubmitting) return@setOnClickListener

                val currentIndex = viewModel.currentQuestionIndex.value ?: 0
                viewModel.selectAnswer(currentIndex, optIndex)

                if (mode == "BLITZ") {
                    val explanation = viewModel.questions.value?.get(currentIndex)?.explanation ?: ""
                    tvTrivia.visibility = View.VISIBLE
                    tvTrivia.text = "Trivia entry: ${explanation.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}"
                }
            }
        }

        btnNext.setOnClickListener {
            if (isSubmitting) return@setOnClickListener

            val currentIndex = viewModel.currentQuestionIndex.value ?: 0
            val total = viewModel.questions.value?.size ?: 0

            if (currentIndex < total - 1) {
                viewModel.currentQuestionIndex.value = currentIndex + 1
            } else {
                // EXAM IS FINISHED - Trigger the network call
                viewModel.submitExamSession(token, mode)
            }
        }
    }

    // --- MOBILE ANTI-CHEAT LOGIC ---
    override fun onPause() {
        super.onPause()
        // If the user backgrounds the app during a Mock Exam and hasn't submitted yet
        if (mode == "MOCK" && !isSubmitting) {
            strikes++
            if (strikes >= 2) {
                Toast.makeText(this, "Session Discarded: Anti-Cheat Violation", Toast.LENGTH_LONG).show()
                // Force submit with "DISCARDED" status
                viewModel.submitExamSession(token, mode, status = "DISCARDED")
            } else {
                Toast.makeText(this, "WARNING: Leaving the app during a Mock Exam will void your session.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // --- UI HELPER ---
    private fun displayQuestion(index: Int) {
        val question = viewModel.questions.value?.get(index) ?: return
        findViewById<TextView>(R.id.tvQuestion).text = question.stem
        findViewById<Button>(R.id.btnA).text = question.optionA
        findViewById<Button>(R.id.btnB).text = question.optionB
        findViewById<Button>(R.id.btnC).text = question.optionC
        findViewById<Button>(R.id.btnD).text = question.optionD
        findViewById<TextView>(R.id.tvTrivia).visibility = View.GONE
    }
}