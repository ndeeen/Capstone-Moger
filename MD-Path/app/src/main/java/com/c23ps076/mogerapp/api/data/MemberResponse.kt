package com.c23ps076.mogerapp.api.data

import com.google.gson.annotations.SerializedName

data class Member(
    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("isHost")
    val isHost: Int
)

