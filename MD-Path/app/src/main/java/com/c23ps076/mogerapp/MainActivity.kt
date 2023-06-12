package com.c23ps076.mogerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c23ps076.mogerapp.api.Preferences
import com.c23ps076.mogerapp.screen.auth.login.LoginActivity

class MainActivity : AppCompatActivity() {
    lateinit var userLogPreference: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userLogPreference = Preferences(this)

        if (userLogPreference.getLoginData().still_login == false) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}