package com.c23ps076.mogerapp.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import retrofit2.converter.gson.GsonConverterFactory

class Retro {
    fun getRetroClientInstance(): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}