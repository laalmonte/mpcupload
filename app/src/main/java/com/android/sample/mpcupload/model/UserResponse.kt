package com.android.sample.mpcupload.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("event")
    val event: String,
    @SerializedName("task")
    val task: String,
    @SerializedName("area")
    val area: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("comments")
    val comments: String,
    @SerializedName("tags")
    val tags: String,
    @SerializedName("photos")
    val photos : List<String>,
    @SerializedName("createAt")
    val createdAt: String
)