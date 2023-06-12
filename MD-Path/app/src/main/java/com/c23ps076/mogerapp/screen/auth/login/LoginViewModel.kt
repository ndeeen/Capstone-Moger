package com.c23ps076.mogerapp.screen.auth.login

import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.Retro
import com.c23ps076.mogerapp.api.UserLoginRequest
import com.c23ps076.mogerapp.api.UserLoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: ViewModel() {
    private val _userLogin = MutableLiveData<UserLoginResponse>()
    val userLogin: LiveData<UserLoginResponse> = _userLogin

    private val _statusLoading = MutableLiveData<Boolean>()
    val statusLoading: LiveData<Boolean> = _statusLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage



    fun doLogin(email: String, password: String) {
        val services = Retro().getRetroClientInstance().create(ApiService::class.java)
        _statusLoading.value = false
        val req = UserLoginRequest(email, password)
        services.loginUser(req)
            .enqueue(object: Callback<UserLoginResponse> {
                override fun onResponse(
                    call: Call<UserLoginResponse>,
                    response: Response<UserLoginResponse>
                ) {
                    _statusLoading.value = false
                    _userLogin.value = response.body()
                    Log.e("respon", response.body().toString())
                    if (response.body()?.status_login == true) {
                        _responseMessage.value = "Login Success"
                    }
                    else {
                        _responseMessage.value = "Login Failed"
                    }
                }

                override fun onFailure(call: Call<UserLoginResponse>, t: Throwable) {
                    _responseMessage.value = t.message
                    _statusLoading.value = false
                }
            })
    }
}