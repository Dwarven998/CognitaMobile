package com.example.cognita.login

import com.example.cognita.api.ApiClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {
    private val loginApi: LoginApi = ApiClient.retrofit.create(LoginApi::class.java)

    fun authenticateUser(request: LoginRequest, onResult: (LoginState) -> Unit) {
        onResult(LoginState.Loading)

        // Change the API call to return ResponseBody to handle plain text
        loginApi.login(request).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val message = response.body()?.string() ?: "Login successful"
                    onResult(LoginState.Success(message))
                } else {
                    onResult(LoginState.Error("Invalid email or password"))
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                onResult(LoginState.Error("Network error: ${t.message}"))
            }
        })
    }
}