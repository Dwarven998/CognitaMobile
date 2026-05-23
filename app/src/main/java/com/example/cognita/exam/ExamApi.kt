package com.example.cognita.exam

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ExamApi {
    @GET("api/exam/generate")
    suspend fun generateExam(
        @Query("mode") mode: String,
        @Query("domain") domain: String? = null
    ): Response<List<Question>>

    @POST("api/exam/submit")
    suspend fun submitSession(
        @Body session: ExamSession
    ): Response<ExamSession>
}