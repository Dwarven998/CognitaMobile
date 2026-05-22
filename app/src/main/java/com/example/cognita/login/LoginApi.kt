package com.example.cognita.login

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/login")
    fun login(@Body request: LoginRequest): Call<ResponseBody>
}