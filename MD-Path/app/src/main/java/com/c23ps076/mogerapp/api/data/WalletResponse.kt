package com.c23ps076.mogerapp.api.data

import com.google.gson.annotations.SerializedName

data class WalletInfo(
    @field:SerializedName("balance")
    val balance: Int,

    @field:SerializedName("walletName")
    val walletName: String
)

data class WalletRequest(
    val partyName: String,
    val walletName: String,
    val balance: Int
)