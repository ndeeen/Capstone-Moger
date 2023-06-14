package com.c23ps076.mogerapp.api.data

import com.google.gson.annotations.SerializedName


data class IncomeOutcome(
    @field:SerializedName("income")
    val income: Int,
    @field:SerializedName("outcome")
    val outcome: Int
)