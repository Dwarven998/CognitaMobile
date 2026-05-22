package com.example.cognita.exam



import com.example.cognita.api.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExamRepository {
    private val api = ApiClient.retrofit.create(ExamApi::class.java)

    fun fetchQuestions(token: String, mode: String, onResult: (List<Question>?) -> Unit) {
        api.generateExam("Bearer $token", mode).enqueue(object : Callback<List<Question>> {
            override fun onResponse(call: Call<List<Question>>, response: Response<List<Question>>) {
                onResult(response.body())
            }
            override fun onFailure(call: Call<List<Question>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    // MAKE SURE THIS FUNCTION IS HERE
    fun submitExam(token: String, session: ExamSessionSubmission, onResult: (Boolean) -> Unit) {
        api.submitExam("Bearer $token", session).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                onResult(response.isSuccessful)
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                onResult(false)
            }
        })
    }
}