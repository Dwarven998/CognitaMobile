package com.example.cognita.exam


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ExamApi {
    @GET("api/exam/generate")
    fun generateExam(
        @Header("Authorization") token: String,
        @Query("mode") mode: String
    ): Call<List<Question>>

    @POST("api/exam/submit")
    fun submitExam(
        @Header("Authorization") token: String,
        @Body session: ExamSessionSubmission
    ): Call<Void>
}