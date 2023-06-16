package com.c23ps076.mogerapp.api.utils

import com.c23ps076.mogerapp.api.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroLazy {
    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://www.wakacipuy.my.id/dokuApp/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}