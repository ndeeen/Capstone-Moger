package com.c23ps076.mogerapp.screen.auth.login

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.databinding.ActivityLoginBinding
import com.c23ps076.mogerapp.BuildConfig.PREF_NAME
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.AuthSession
import com.c23ps076.mogerapp.screen.auth.PasswordCustomView
import com.c23ps076.mogerapp.screen.groupList.GroupListActivity

class LoginActivity : AppCompatActivity() {
    private var activityLoginBinding: ActivityLoginBinding? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var loginUserPreferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityLoginBinding?.root)

        showLoading(false)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        loginViewModel.responseMessage.observe(this) {
            showMessage(it)
        }

        pref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        loginUserPreferences = Preferences(this)

        activityLoginBinding?.apply {
            button.setOnClickListener {
                validateLoginField()
            }
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this ,message, Toast.LENGTH_LONG).show()
    }

    private fun validateLoginField() {
        val emailInputText = activityLoginBinding?.etEmail?.text?.toString()
        val passwordInputText = PasswordCustomView(this)

        passwordInputText.text = activityLoginBinding?.etPassword?.text

        if (emailInputText == "") {
            activityLoginBinding?.etEmail?.error = getString(R.string.emailRequired)
            return
        }
        else if (passwordInputText?.text?.isBlank() == true) {
            passwordInputText.error = getString(R.string.passRequired)
            return
        }
        else if (!passwordInputText.isPasswordValid()) {
            return
        }
        else {
            login()
        }
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            activityLoginBinding?.progressBar?.visibility = View.VISIBLE
        }
        else {
            activityLoginBinding?.progressBar?.visibility = View.INVISIBLE
        }
    }

    fun login(){
        val emailLogin: String = activityLoginBinding?.etEmail?.text.toString().trim()
        val passwordLogin: String = activityLoginBinding?.etPassword?.text.toString().trim()

        loginViewModel.apply {
            doLogin(emailLogin, passwordLogin)
            responseMessage.observe(this@LoginActivity) {message ->
                if (!message.isNullOrBlank()) {
                    showMessage(message)
                }
            }
            userLogin.observe(this@LoginActivity) {user ->
                try {
                    user?.let {
                        val currentUser = AuthSession(
                            it.name, it.access_token, true, activityLoginBinding?.etEmail?.text.toString().trim()
                        )
                        loginUserPreferences.setUserLogin(currentUser)
                        startActivity(Intent(this@LoginActivity, GroupListActivity::class.java))
                        this@LoginActivity.finish()
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }

        }

    }
}