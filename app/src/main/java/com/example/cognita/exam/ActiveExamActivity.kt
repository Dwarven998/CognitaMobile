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

    private val EXAM_DURATION_MILLIS: Long = 15 * 60 * 1000
    private val USER_EMAIL = "algian.aquillo@gmail.com" // Replace with real auth data
    private val EXAM_MODE = "MOCK"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_exam)

        tvTimer = findViewById(R.id.tvTimer)
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress)
        pbExamProgress = findViewById(R.id.pbExamProgress)
        tvQuestionText = findViewById(R.id.tvQuestionText)
        rgOptions = findViewById(R.id.rgOptions)
        rbOptionA = findViewById(R.id.rbOptionA)
        rbOptionB = findViewById(R.id.rbOptionB)
        rbOptionC = findViewById(R.id.rbOptionC)
        rbOptionD = findViewById(R.id.rbOptionD)
        btnNext = findViewById(R.id.btnNext)

        viewModel = ViewModelProvider(this)[ExamViewModel::class.java]

        setupObservers()
        setupListeners()

        // Fetch questions from Spring Boot
        viewModel.fetchQuestions(EXAM_MODE)

        startTimer(EXAM_DURATION_MILLIS)
    }

    private fun setupObservers() {
        viewModel.currentQuestion.observe(this) { question ->
            if (question != null) {
                tvQuestionText.text = question.text
                rbOptionA.text = question.optionA
                rbOptionB.text = question.optionB
                rbOptionC.text = question.optionC
                rbOptionD.text = question.optionD

                rgOptions.clearCheck()
            }
        }

        viewModel.progressState.observe(this) { progress ->
            tvQuestionProgress.text = "Question ${progress.current} of ${progress.total}"
            pbExamProgress.max = progress.total
            pbExamProgress.progress = progress.current

            if (progress.current == progress.total) {
                btnNext.text = "Submit Exam"
            } else {
                btnNext.text = "Next Question"
            }
        }

        viewModel.examFinishedEvent.observe(this) { isFinished ->
            if (isFinished) {
                finishExam()
            }
        }
    }

    private fun setupListeners() {
        btnNext.setOnClickListener {
            val selectedOptionId = rgOptions.checkedRadioButtonId

            if (selectedOptionId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedAnswer = when (selectedOptionId) {
                R.id.rbOptionA -> "A"
                R.id.rbOptionB -> "B"
                R.id.rbOptionC -> "C"
                R.id.rbOptionD -> "D"
                else -> ""
            }

            viewModel.submitAnswerAndNext(selectedAnswer, USER_EMAIL, EXAM_MODE)
        }
    }

    private fun startTimer(durationMillis: Long) {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(durationMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                tvTimer.text = String.format("%02d:%02d", minutes, seconds)

                if (millisUntilFinished < 60000) {
                    tvTimer.setTextColor(resources.getColor(android.R.color.holo_red_dark, theme))
                }
            }

            override fun onFinish() {
                tvTimer.text = "00:00"
                Toast.makeText(this@ActiveExamActivity, "Time is up!", Toast.LENGTH_LONG).show()
                viewModel.forceSubmitExam(USER_EMAIL, EXAM_MODE)
            }
        }.start()
    }

    private fun finishExam() {
        countDownTimer?.cancel()
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra("SCORE", viewModel.finalScore)
            putExtra("TOTAL", viewModel.totalQuestions)
        }
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}