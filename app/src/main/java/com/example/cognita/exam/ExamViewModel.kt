package com.example.cognita.exam

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ExamViewModel : ViewModel() {

    // Use the repository instead of creating the API interface directly here
    private val repository = ExamRepository()

    private val _questions = MutableLiveData<List<Question>>()

    private val _currentQuestionIndex = MutableLiveData(0)

    private val _currentQuestion = MutableLiveData<Question?>()
    val currentQuestion: LiveData<Question?> = _currentQuestion

    private val _progressState = MutableLiveData<ExamProgress>()
    val progressState: LiveData<ExamProgress> = _progressState

    private val _examFinishedEvent = MutableLiveData<Boolean>()
    val examFinishedEvent: LiveData<Boolean> = _examFinishedEvent

    var finalScore: Int = 0
    var totalQuestions: Int = 0

    private val userAnswers = mutableMapOf<Int, String>()

    fun fetchQuestions(mode: String = "MOCK", domain: String? = null) {
        viewModelScope.launch {
            try {
                // Call the repository layer
                val response = repository.fetchQuestions(mode, domain)
                if (response.isSuccessful && response.body() != null) {
                    val qList = response.body()!!
                    _questions.value = qList
                    totalQuestions = qList.size

                    if (qList.isNotEmpty()) {
                        updateStateForIndex(0)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun submitAnswerAndNext(selectedAnswer: String, userEmail: String, mode: String) {
        val currentIndex = _currentQuestionIndex.value ?: 0
        userAnswers[currentIndex] = selectedAnswer

        val qList = _questions.value ?: return

        if (currentIndex < qList.size - 1) {
            updateStateForIndex(currentIndex + 1)
        } else {
            submitFinalExam(userEmail, mode)
        }
    }

    fun forceSubmitExam(userEmail: String, mode: String) {
        submitFinalExam(userEmail, mode)
    }

    private fun submitFinalExam(userEmail: String, mode: String) {
        val qList = _questions.value ?: return

        var score = 0
        for (i in qList.indices) {
            if (userAnswers[i] == qList[i].correctAnswer) {
                score++
            }
        }

        finalScore = score

        val sessionPayload = ExamSession(
            userEmail = userEmail,
            mode = mode,
            score = score,
            totalItems = totalQuestions
        )

        viewModelScope.launch {
            try {
                val response = repository.submitExam(sessionPayload)
                if (response.isSuccessful) {
                    _examFinishedEvent.value = true
                } else {
                    _examFinishedEvent.value = true
                }
            } catch (e: Exception) {
                _examFinishedEvent.value = true
            }
        }
    }

    private fun updateStateForIndex(index: Int) {
        _currentQuestionIndex.value = index
        _currentQuestion.value = _questions.value?.get(index)
        _progressState.value = ExamProgress(index + 1, totalQuestions)
    }
}