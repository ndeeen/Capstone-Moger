package com.c23ps076.mogerapp.api
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import com.c23ps076.mogerapp.BuildConfig.BASE_URL

import com.c23ps076.mogerapp.api.UserLoginResponse
import com.c23ps076.mogerapp.api.UserLoginRequest
import com.c23ps076.mogerapp.api.data.*

interface ApiService {
    @POST("login")
    fun loginUser(
        @Body request: UserLoginRequest
    ): Call<UserLoginResponse>

    @POST("register")
    fun registerUser(
        @Body request: UserRegisterRequest
    ): Call<UserRegisterResponse>

    @GET("getWallet/{partyName}")
    fun getWallet(
        @Path("partyName") partyName: String
    ): Call<ArrayList<WalletInfo>>

    @GET("getPartyList/{email}")
    fun getPartyList(
        @Path("email") email: String
    ): Call<ArrayList<GroupInfo>>

    @GET("getIncomeOutcome/{partyName}/{month}/{year}")
    fun getIncomeOutcome(
        @Path("partyName") partyName: String,
        @Path("month") month: String,
        @Path("year") year: String
    ): Call<IncomeOutcome>

    @GET("getTransaction/{partyName}/{month}/{year}")
    fun getTransaction(
        @Path("partyName") partyName: String,
        @Path("month") month: String,
        @Path("year") year: String
    ): Call<ArrayList<TransactionInfo>>

    @GET("getMemberLists/{partyName}")
    fun getMemberLists(
        @Path("partyName") partyName: String
    ): Call<ArrayList<Member>>

    @POST("addMember/{email}/{partyName}")
    fun addMember(
        @Path("email") email: String,
        @Path("partyName") partyName: String
    ): Call<Int>

    @POST("deleteMember/{email}/{partyName}")
    fun deleteMember(
        @Path("email") email: String,
        @Path("partyName") partyName: String
    ): Call<Int>

    @POST("addTransaction/{partyName}/{createdBy}/{kind}/{fromWallet}/{toWallet}/{amount}/{stamp}/{incomeOutcomeKind}")
    fun addTransaction(
        @Path("partyName") partyName: String,
        @Path("createdBy") createdBy: String,
        @Path("kind") kind: String,
        @Path("fromWallet") fromWallet: String,
        @Path("toWallet") toWallet: String,
        @Path("amount") amount: String,
        @Path("stamp") stamp: String,
        @Path("incomeOutcomeKind") incomeOutcomeKind: String
    ): Call<Int>





    companion object {
        private const val TOKEN_SAMPLE =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyb2ZpZkBnbWFpbC5jb20iLCJleHAiOjE2ODY1NTcxMjJ9.mg7UsqtOHyvejbsActoEm-oUUmjkkvPHgxCLpE93IQU"

        private val gson = GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        fun create(url: String): ApiService {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(url)
                .client(okHttpClient)
                .build()
                .create(ApiService::class.java)
        }

    }
}