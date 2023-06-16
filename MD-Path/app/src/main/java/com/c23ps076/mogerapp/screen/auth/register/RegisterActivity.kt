package com.c23ps076.mogerapp.screen.auth.register

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.c23ps076.mogerapp.BuildConfig.PREF_NAME
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.databinding.ActivityRegisterBinding
import com.c23ps076.mogerapp.screen.auth.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private var activityRegisterBinding: ActivityRegisterBinding? = null
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var userLogPreferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(activityRegisterBinding?.root)

        showLoading(false)

        supportActionBar?.hide()

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        registerViewModel.responseMessage.observe(this) {
            showMessage(it)
        }

        pref = this.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        userLogPreferences = Preferences(this)

        activityRegisterBinding?.apply {
            btnRegister.setOnClickListener {
                showLoading(true)
                validateRegisterField()
            }
            btnBack.setOnClickListener {
                backToLogin()
            }
        }

    }

    private fun validateRegisterField() {
        val regName = activityRegisterBinding?.etRegName?.text?.toString()?.trim()
        val regEmail = activityRegisterBinding?.etRegEmail?.text?.toString()?.trim()
        val regPassword = activityRegisterBinding?.etRegPassword

        if (regName.isNullOrBlank()) {
            activityRegisterBinding?.etRegName?.error = getString(R.string.nameRequired)
        }
        else if (regEmail.isNullOrBlank()) {
            activityRegisterBinding?.etRegEmail?.error = getString(R.string.emailRequired)
        }
        else if (!regPassword?.isPasswordValid()!!) {
            activityRegisterBinding?.etRegPassword?.error = getString(R.string.passRule)
        }
        else {
            register()
        }
    }

    private fun register() {
        val name = activityRegisterBinding?.etRegName?.text?.toString()?.trim()
        val email = activityRegisterBinding?.etRegEmail?.text?.toString()?.trim()
        val password = activityRegisterBinding?.etRegPassword?.text?.toString()?.trim()

        registerViewModel.apply {
            showLoading(true)
            if (name != null) {
                if (email != null) {
                    if (password != null) {
                        doRegister(email, name, password)
                    }
                }
            }
            responseMessage.observe(this@RegisterActivity) { message ->
                if (!message.isNullOrBlank()) {
                    showMessage(message)
                    if (message == getString(R.string.userCreated)) {
                        backToLogin()
                    }
                }
                showLoading(false)
            }
        }
    }

    private fun backToLogin() {
        finishAffinity()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun showMessage(message: String) {
        Toast.makeText(this ,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            activityRegisterBinding?.progressBar?.visibility = View.VISIBLE
        }
        else {
            activityRegisterBinding?.progressBar?.visibility = View.INVISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityRegisterBinding = null
    }
}