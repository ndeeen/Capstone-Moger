package com.c23ps076.mogerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.c23ps076.mogerapp.api.Preferences
import com.c23ps076.mogerapp.screen.auth.login.LoginActivity
import com.c23ps076.mogerapp.screen.dummyscreen.DummyActivity

class MainActivity : AppCompatActivity() {
    lateinit var userLogPreference: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userLogPreference = Preferences(this)
        Log.e("logMainPref", userLogPreference.getLoginData().toString())
        if (userLogPreference.getLoginData().still_login == false) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            val intent = Intent(this, DummyActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}