package com.c23ps076.mogerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.screen.auth.login.LoginActivity
import com.c23ps076.mogerapp.screen.groupList.GroupListActivity

class MainActivity : AppCompatActivity() {
    lateinit var userLogPreference: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userLogPreference = Preferences(this)
        if (userLogPreference.getLoginData().still_login == false) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            val intent = Intent(this, GroupListActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}