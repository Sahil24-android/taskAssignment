package com.task.taskapplication.network

import com.task.taskapplication.data.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {

    @GET("users?page=2")
    suspend fun getUsers():Response<UserResponse>
}