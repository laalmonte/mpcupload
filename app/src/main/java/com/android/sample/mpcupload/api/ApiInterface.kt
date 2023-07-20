package com.android.sample.mpcupload.api

import com.android.sample.mpcupload.model.DataResult
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
//    https://reqres.in/api/users
    @GET("/api/users")
    suspend fun getUsers(): Response<DataResult>

}