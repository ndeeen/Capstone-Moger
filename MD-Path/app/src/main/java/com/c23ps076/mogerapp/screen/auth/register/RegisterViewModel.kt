package com.c23ps076.mogerapp.screen.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel: ViewModel() {
    private val _userRegister = MutableLiveData<UserRegisterResponse>()
    val userRegister: LiveData<UserRegisterResponse> = _userRegister

    private val _statusLoading = MutableLiveData<Boolean>()
    val statusLoading: LiveData<Boolean> = _statusLoading

    private val _responseMessage = MutableLiveData<String>()
    val responseMessage: LiveData<String> = _responseMessage

    fun doRegister(email: String, name: String, password: String) {
        val services = ApiService.create(BASE_URL)
        _statusLoading.value = false
        val req = UserRegisterRequest(email, name, password)
        services.registerUser(req)
            .enqueue(object: Callback<UserRegisterResponse> {
                override fun onResponse(
                    call: Call<UserRegisterResponse>,
                    response: Response<UserRegisterResponse>
                ) {
                    _statusLoading.value = false
                    _userRegister.value = response.body()
                    if (response.body()?.message.toString() == "User registered successfully") {
                        _responseMessage.value = "User registered successfully"
                    }
                    else {
                        _responseMessage.value = "Register failed"
                    }
                }

                override fun onFailure(call: Call<UserRegisterResponse>, t: Throwable) {
                    _responseMessage.value = t.message
                    _statusLoading.value = false
                }

            })
    }

}