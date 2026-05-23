package com.example.cognita.exam

import com.example.cognita.api.ApiClient
import retrofit2.Response

/**
 * Repository for fetching questions from the backend API.
 * Provides a clean data-access layer for ViewModels.
 */
class QuestionRepository {

    private val api = ApiClient.retrofit.create(ExamApi::class.java)

    /**
     * Fetches questions for a given domain from the backend.
     * @param domain The subject domain to filter questions by (e.g., "MATH", "SCIENCE").
     * @return A Retrofit Response wrapping the list of questions.
     */
    suspend fun findByDomain(domain: String): Response<List<Question>> {
        return api.generateExam(mode = "MOCK", domain = domain)
    }

    /**
     * Fetches questions for an exam by mode and optional domain.
     * @param mode The exam mode (e.g., "MOCK", "PRACTICE").
     * @param domain Optional domain filter.
     * @return A Retrofit Response wrapping the list of questions.
     */
    suspend fun fetchQuestions(mode: String, domain: String? = null): Response<List<Question>> {
        return api.generateExam(mode, domain)
    }
}