package com.c23ps076.mogerapp.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import retrofit2.converter.gson.GsonConverterFactory

class Retro {
    fun getRetroClientInstance(url: String): Retrofit {
        val gson = GsonBuilder().setLenient().create()
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}