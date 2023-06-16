package com.c23ps076.mogerapp.api.data

import com.google.gson.annotations.SerializedName

data class WalletInfo(
    @field:SerializedName("ballance")
    val balance: Int,

    @field:SerializedName("walletName")
    val walletName: String
)