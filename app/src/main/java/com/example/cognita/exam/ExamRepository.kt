package com.example.cognita.exam

import com.example.cognita.api.ApiClient
import retrofit2.Response

class ExamRepository {
    private val api = ApiClient.retrofit.create(ExamApi::class.java)

    suspend fun fetchQuestions(mode: String, domain: String? = null): Response<List<Question>> {
        return api.generateExam(mode, domain)
    }

    suspend fun submitExam(session: ExamSession): Response<ExamSession> {
        return api.submitSession(session)
    }
}