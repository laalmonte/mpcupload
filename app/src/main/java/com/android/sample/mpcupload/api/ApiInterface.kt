package com.android.sample.mpcupload.api

import com.android.sample.mpcupload.model.DataResult
import com.android.sample.mpcupload.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
//    https://reqres.in/api/users
    @GET("/api/users")
    suspend fun getUsers(): Response<DataResult>

    @FormUrlEncoded
    @POST("/api/users")
    suspend fun postUser(
        @Field("event") event: String,
        @Field("task") task: String,
        @Field("area") area: String,
        @Field("date") date: String,
        @Field("comments") comments: String,
        @Field("tags") tags: String,
        @Field("photos") photos: List<String>): Response<UserResponse>
}