package com.c23ps076.mogerapp.screen.creategroup

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.CreateGroupRequest
import com.c23ps076.mogerapp.api.data.MessageResponse
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.databinding.ActivityCreateGroupBinding
import com.c23ps076.mogerapp.screen.groupList.GroupListActivity
import kotlinx.android.synthetic.main.activity_create_group.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateGroupActivity : AppCompatActivity() {
    private lateinit var activityCreateGroupBinding: ActivityCreateGroupBinding
    private lateinit var userLogPreferences: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityCreateGroupBinding = ActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(activityCreateGroupBinding.root)
        userLogPreferences = Preferences(this)
        showLoading(false)
        supportActionBar?.title = getString(R.string.barCreateNewGroup)
        supportActionBar?.show()
        activityCreateGroupBinding.apply {
            btnAddGroup.setOnClickListener {
                showLoading(true)
                addGroup()
            }
        }
    }

    private fun addGroup() {
        if (et_groupname.text != null) {
            val service = ApiService.create(BASE_URL)
            val request = CreateGroupRequest(et_groupname.text.toString(), userLogPreferences.getLoginData().email.toString())
            service.createParty(request).enqueue(object : Callback<MessageResponse> {
                override fun onResponse(
                    call: Call<MessageResponse>,
                    response: Response<MessageResponse>
                ) {
                    showMessage(getString(R.string.groupAdded))
                    showLoading(false)
                    moveBack()
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    showMessage(getString(R.string.groupAddFailed))
                    showLoading(false)
                }

            })
        }

    }

    private fun moveBack() {
        val intentBack = Intent(this, GroupListActivity::class.java)
        finishAffinity()
        startActivity(intentBack)
    }

    private fun showMessage(message: String) {
        Toast.makeText(this ,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            activityCreateGroupBinding?.progressBar7?.visibility = View.VISIBLE
        }
        else {
            activityCreateGroupBinding?.progressBar7?.visibility = View.INVISIBLE
        }
    }

}