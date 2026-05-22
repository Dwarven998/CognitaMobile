package com.example.cognita.exam

data class Question(
    val id: Long,
    val domain: String,
    val stem: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int,
    val explanation: String
)

data class ExamSessionSubmission(
    val examType: String,
    val score: Int,
    val totalItems: Int,
    val status: String
)
// Tracks the network state when pushing results
sealed class ExamSubmissionState {
    object Idle : ExamSubmissionState()
    object Submitting : ExamSubmissionState()
    data class Success(val score: Int, val total: Int) : ExamSubmissionState()
    data class Error(val message: String) : ExamSubmissionState()
}