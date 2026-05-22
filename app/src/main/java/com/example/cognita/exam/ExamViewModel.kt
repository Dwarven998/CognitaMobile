package com.example.cognita.exam


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExamViewModel : ViewModel() {
    private val repository = ExamRepository()

    val questions = MutableLiveData<List<Question>>()
    val currentQuestionIndex = MutableLiveData(0)
    val userAnswers = mutableMapOf<Int, Int>()

    // NEW: State tracker for submission
    private val _submissionState = MutableLiveData<ExamSubmissionState>(ExamSubmissionState.Idle)
    val submissionState: LiveData<ExamSubmissionState> = _submissionState

    fun loadExam(token: String, mode: String) {
        repository.fetchQuestions(token, mode) { result ->
            if (result != null) {
                questions.value = result
            }
        }
    }

    fun selectAnswer(questionIndex: Int, optionIndex: Int) {
        userAnswers[questionIndex] = optionIndex
    }

    private fun calculateScore(): Int {
        var score = 0
        questions.value?.forEachIndexed { index, question ->
            if (userAnswers[index] == question.correctOptionIndex) {
                score++
            }
        }
        return score
    }

    // NEW: The function that builds the payload and sends it to the server
    fun submitExamSession(token: String, mode: String, status: String = "COMPLETED") {
        _submissionState.value = ExamSubmissionState.Submitting

        val score = calculateScore()
        val total = questions.value?.size ?: 0

        val submission = ExamSessionSubmission(
            examType = mode,
            score = score,
            totalItems = total,
            status = status
        )

        repository.submitExam(token, submission) { success ->
            if (success) {
                _submissionState.value = ExamSubmissionState.Success(score, total)
            } else {
                _submissionState.value = ExamSubmissionState.Error("Failed to sync with server")
            }
        }
    }
}