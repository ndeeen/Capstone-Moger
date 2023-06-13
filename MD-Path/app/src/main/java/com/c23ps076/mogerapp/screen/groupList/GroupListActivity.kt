package com.c23ps076.mogerapp.screen.groupList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.databinding.ActivityGroupListBinding
import com.c23ps076.mogerapp.screen.profile.ProfileActivity

class GroupListActivity : AppCompatActivity() {
    private lateinit var activityGroupListBinding: ActivityGroupListBinding
    lateinit var userLogPreferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGroupListBinding = ActivityGroupListBinding.inflate(layoutInflater)
        setContentView(activityGroupListBinding.root)
        supportActionBar?.title = getString(R.string.grouplist)
        supportActionBar?.show()

        userLogPreferences = Preferences(this)
        Log.e("prefGroupList", userLogPreferences.getLoginData().toString())
        initialize()
    }

    private fun initialize() {
        activityGroupListBinding.apply {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            else -> {return super.onOptionsItemSelected(item)}
        }
    }
}