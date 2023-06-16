package com.c23ps076.mogerapp.screen.profile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.databinding.ActivityProfileBinding
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.screen.auth.login.LoginActivity
import com.c23ps076.mogerapp.screen.groupList.GroupListActivity

class ProfileActivity : AppCompatActivity() {
    private lateinit var activityProfileBinding: ActivityProfileBinding
    lateinit var userLogPreferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityProfileBinding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(activityProfileBinding.root)
        userLogPreferences = Preferences(this)

        supportActionBar?.hide()

        initialize()
    }

    private fun initialize() {
        activityProfileBinding.apply {
            tvProfileName.text = userLogPreferences.getLoginData().name.toString()
            tvProfileEmail.text = userLogPreferences.getLoginData().email.toString()

            btnLogout.setOnClickListener {
                logout()
            }
            btnProfileBack.setOnClickListener {
                moveBack()
            }
        }
    }

    private fun moveBack() {
        val intentBack = Intent(this, GroupListActivity::class.java)
        finishAffinity()
        startActivity(intentBack)
    }
    private fun logout() {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.wantLogoutQuestion))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                userLogPreferences.clearLoginData()
                userLogPreferences.logout()

                finishAffinity()
                val intentLogout = Intent(this, LoginActivity::class.java)
                startActivity(intentLogout)
                this.finish()
            }
            .setNegativeButton(getString(R.string.no), null)
            .create()

        alertDialog.show()
    }
}