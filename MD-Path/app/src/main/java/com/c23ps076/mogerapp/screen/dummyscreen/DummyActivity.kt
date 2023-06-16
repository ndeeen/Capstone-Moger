package com.c23ps076.mogerapp.screen.dummyscreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.utils.Preferences

class DummyActivity : AppCompatActivity() {
    private lateinit var userLogPreference: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dummy)
        userLogPreference = Preferences(this)
        Log.e("dummyPref", userLogPreference.getLoginData().toString())
    }
}