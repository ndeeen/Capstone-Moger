package com.c23ps076.mogerapp.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
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

    @GET("getMonthlyTransactions/{partyName}/{month}/{year}")
    fun getTransaction(
        @Path("partyName") partyName: String,
        @Path("month") month: String,
        @Path("year") year: String
    ): Call<ArrayList<TransactionInfo>>

    @GET("getMemberLists/{partyName}")
    fun getMemberLists(
        @Path("partyName") partyName: String
    ): Call<ArrayList<Member>>

    @POST("addMember")
    fun addMember(
        @Body request: MemberRequest
    ): Call<MessageResponse>

    @POST("deleteMember")
    fun deleteMember(
        @Body request: MemberRequest
    ): Call<MessageResponse>

    @POST("addTransaction")
    fun addTransaction(
        @Body request: TransactionRequest
    ): Call<MessageResponse>

    @GET("getBalance/{partyName}")
    fun getBalance(
        @Path("partyName") partyName: String
    ): Call<ArrayList<BalanceInfo>>

    @POST("addWallet")
    fun addWallet(
        @Body request: WalletRequest
    ): Call<MessageResponse>

    @POST("deleteWalletByPartyAndWallet/{partyName}/{walletName}")
    fun deleteWallet(
        @Path("partyName") partyName: String,
        @Path("walletName") walletName: String
    ): Call<MessageResponse>

    @POST("deleteTransaction/{transactionId}")
    fun deleteTransaction(
        @Path("transactionId") transactionId: Int
    ): Call<MessageResponse>

    @POST("createParty")
    fun createParty(
        @Body request: CreateGroupRequest
    ): Call<MessageResponse>



    companion object {
        private const val TOKEN_SAMPLE =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyb2ZpZkBnbWFpbC5jb20iLCJleHAiOjE2ODY1NTcxMjJ9.mg7UsqtOHyvejbsActoEm-oUUmjkkvPHgxCLpE93IQU"

        private val gson = GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
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