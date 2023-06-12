package com.c23ps076.mogerapp.api
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AuthSession(
    var name: String,
    var access_token: String,
    var still_login: Boolean
)

data class UserLoginRequest(
    @SerializedName("email")
    var email: String,

    @SerializedName("password")
    var password: String
)
data class UserLoginResponse(
    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("access_token")
    var access_token: String,

    @field:SerializedName("status_login")
    var status_login: Boolean
)


