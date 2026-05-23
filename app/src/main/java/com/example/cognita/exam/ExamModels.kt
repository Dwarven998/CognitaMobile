package com.example.cognita.exam

import com.google.gson.annotations.SerializedName

data class Question(
    val id: Long,

    @SerializedName("stem")
    val text: String = "",        // Backend sends "stem", UI uses "text"

    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",

    @SerializedName("correctOptionIndex")
    val correctOptionIndexRaw: Int = 0,  // Backend sends 0=A, 1=B, 2=C, 3=D

    val explanation: String? = null,
    val domain: String? = null
) {
    /** Converts the integer index (0-3) to a letter ("A"-"D") for answer comparison */
    val correctAnswer: String
        get() = when (correctOptionIndexRaw) {
            0 -> "A"
            1 -> "B"
            2 -> "C"
            3 -> "D"
            else -> ""
        }
}

data class ExamSession(
    val id: Long? = null,
    val userEmail: String,
    val mode: String,
    val score: Int,
    val totalItems: Int,
    val completedAt: String? = null
)

data class ExamProgress(val current: Int, val total: Int)