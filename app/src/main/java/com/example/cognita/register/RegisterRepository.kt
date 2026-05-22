package com.example.cognita.register

import com.example.cognita.api.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterRepository {
    private val api = ApiClient.retrofit.create(RegisterApi::class.java)

    fun registerUser(request: RegisterRequest, onResult: (RegisterState) -> Unit) {
        api.register(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // This reads the "Registration successful" string from your backend
                    val message = response.body()?.string() ?: "Success"
                    onResult(RegisterState.Success(message))
                } else {
                    val errorMsg = response.errorBody()?.string()
                    onResult(RegisterState.Error("Failed: ${response.code()} - ${errorMsg ?: "Unknown"}"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResult(RegisterState.Error("Network error: ${t.message}"))
            }
        })
    }
}
// DELETE THE "private fun <T> Call<T>.enqueue" BLOCK BELOW THIS LINE