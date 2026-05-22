package com.example.cognita.register

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApi {
    @POST("api/register")
    fun register(@Body request: RegisterRequest): Call<ResponseBody>
}