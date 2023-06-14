package com.c23ps076.mogerapp.api.data

import com.google.gson.annotations.SerializedName

data class GroupInfo(
    @field:SerializedName("ballance")
    val ballance: ArrayList<BalanceInfo>,

    @field:SerializedName("members")
    val members: ArrayList<MemberEmail>,

    @field:SerializedName("partyName")
    val partyName: String
    )

data class BalanceInfo(
    @field:SerializedName("ballance")
    val ballance: Int
)

data class MemberEmail(
    @field:SerializedName("email")
    val email: String
)