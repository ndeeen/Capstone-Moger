package com.c23ps076.mogerapp.api.data

import com.google.gson.annotations.SerializedName

data class TransactionInfo(
    @field:SerializedName("day")
    val day: Int,

    @field:SerializedName("expense")
    val expense: Int,

    @field:SerializedName("transactions")
    val transactions: ArrayList<transaction>
)

data class transaction(
    @field:SerializedName("IncomeOutcomeKind")
    val category: String?,

    @field:SerializedName("amount")
    val amount: Int,

    @field:SerializedName("createdBy")
    val createdBy: String,

    @field:SerializedName("fromWallet")
    val from: String,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("kind")
    val kind: String,

    @field:SerializedName("toWallet")
    val toWallet: String?
)