package com.example.cognita.exam

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cognita.R
import com.example.cognita.results.ResultsActivity
import java.util.concurrent.TimeUnit

class ActiveExamActivity : AppCompatActivity() {

    private lateinit var viewModel: ExamViewModel
    private var countDownTimer: CountDownTimer? = null

    // UI Elements
    private lateinit var tvTimer: TextView
    private lateinit var tvQuestionProgress: TextView
    private lateinit var pbExamProgress: ProgressBar
    private lateinit var tvQuestionText: TextView
    private lateinit var rgOptions: RadioGroup
    private lateinit var rbOptionA: RadioButton
    private lateinit var rbOptionB: RadioButton
    private lateinit var rbOptionC: RadioButton
    private lateinit var rbOptionD: RadioButton
    private lateinit var btnNext: Button

    // Set dynamic duration based on API later; hardcoded to 15 mins for now
    private val EXAM_DURATION_MILLIS: Long = 15 * 60 * 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_exam)

        // Initialize UI Elements with findViewById
        tvTimer = findViewById(R.id.tvTimer)
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress)
        pbExamProgress = findViewById(R.id.pbExamProgress)
        tvQuestionText = findViewById(R.id.tvQuestionText) // Matches XML precisely
        rgOptions = findViewById(R.id.rgOptions)
        rbOptionA = findViewById(R.id.rbOptionA)
        rbOptionB = findViewById(R.id.rbOptionB)
        rbOptionC = findViewById(R.id.rbOptionC)
        rbOptionD = findViewById(R.id.rbOptionD)
        btnNext = findViewById(R.id.btnNext)

        viewModel = ViewModelProvider(this)[ExamViewModel::class.java]

        setupObservers()
        setupListeners()

        // Trigger API fetch for questions from the ViewModel
        // viewModel.fetchQuestions()

        startTimer(EXAM_DURATION_MILLIS)
    }

    private fun setupObservers() {
        // Observe Current Question mapping to the dynamic UI
        /*
        viewModel.currentQuestion.observe(this) { question ->
            if (question != null) {
                tvQuestionText.text = question.text
                rbOptionA.text = question.optionA
                rbOptionB.text = question.optionB
                rbOptionC.text = question.optionC
                rbOptionD.text = question.optionD

                // Clear the previously selected option visually
                rgOptions.clearCheck()
            }
        }

        // Observe Exam Progress to update progress bar
        viewModel.progressState.observe(this) { progress ->
            tvQuestionProgress.text = "Question ${progress.current} of ${progress.total}"
            pbExamProgress.max = progress.total
            pbExamProgress.progress = progress.current

            // Switch button text on final question
            if (progress.current == progress.total) {
                btnNext.text = "Submit Exam"
            } else {
                btnNext.text = "Next Question"
            }
        }

        // Observe when the backend calculates/finishes the exam
        viewModel.examFinishedEvent.observe(this) { isFinished ->
            if (isFinished) {
                finishExam()
            }
        }
        */
    }

    private fun setupListeners() {
        btnNext.setOnClickListener {
            val selectedOptionId = rgOptions.checkedRadioButtonId

            if (selectedOptionId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Map standard RadioButton selections to Data Layer answers
            val selectedAnswer = when (selectedOptionId) {
                R.id.rbOptionA -> "A"
                R.id.rbOptionB -> "B"
                R.id.rbOptionC -> "C"
                R.id.rbOptionD -> "D"
                else -> ""
            }

            // Send to backend/ViewModel logic
            // viewModel.submitAnswerAndNext(selectedAnswer)

            // Temporary progression for testing without backend connection
            Toast.makeText(this, "Answer $selectedAnswer recorded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startTimer(durationMillis: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                tvTimer.text = String.format("%02d:%02d", minutes, seconds)

                // Turn timer text Red when < 1 minute remains
                if (millisUntilFinished < 60000) {
                    tvTimer.setTextColor(resources.getColor(android.R.color.holo_red_dark, theme))
                }
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                Toast.makeText(this@ActiveExamActivity, "Time is up!", Toast.LENGTH_LONG).show()
                // Force submit via backend
                // viewModel.forceSubmitExam()
                finishExam()
            }
        }.start()
    }

    private fun finishExam() {
        countDownTimer?.cancel()
        val intent = Intent(this, ResultsActivity::class.java)
        // Pass exam ID or session token via intent if required by your backend
        startActivity(intent)
        finish() // Removes ActiveExamActivity from the back stack
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel() // Prevent memory leaks
    }
}