package com.c23ps076.mogerapp.screen.groupList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.GroupInfo
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.databinding.ActivityGroupListBinding
import com.c23ps076.mogerapp.screen.creategroup.CreateGroupActivity
import com.c23ps076.mogerapp.screen.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_group_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupListActivity : AppCompatActivity() {
    private lateinit var activityGroupListBinding: ActivityGroupListBinding
    lateinit var userLogPreferences: Preferences
    private val listGroup = ArrayList<GroupInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGroupListBinding = ActivityGroupListBinding.inflate(layoutInflater)
        setContentView(activityGroupListBinding.root)
        supportActionBar?.title = getString(R.string.grouplist)
        supportActionBar?.show()

        showLoading(true)
        userLogPreferences = Preferences(this)


        initialize()

        activityGroupListBinding.apply {
            fabCreateGroup.setOnClickListener {
                val intentCreateGroup = Intent(this@GroupListActivity, CreateGroupActivity::class.java)
                startActivity(intentCreateGroup)
            }
        }
    }
    private fun initialize() {
        activityGroupListBinding.apply {
            rvGrouplist.setHasFixedSize(true)
            rvGrouplist.layoutManager = LinearLayoutManager(this@GroupListActivity)
        }
        getGroupList(userLogPreferences.getLoginData().email)
    }

    private fun getGroupList(email: String) {
        val service = ApiService.create(BASE_URL)
        service.getPartyList(email).enqueue(object : Callback<ArrayList<GroupInfo>> {
            override fun onResponse(
                call: Call<ArrayList<GroupInfo>>,
                response: Response<ArrayList<GroupInfo>>
            ) {
                response.body()?.let {
                    listGroup.addAll(it)
                }
                val adapter = GroupListParentAdapter(listGroup, userLogPreferences.getLoginData().email.toString())
                rv_grouplist.adapter = adapter
                showLoading(false)
            }

            override fun onFailure(call: Call<ArrayList<GroupInfo>>, t: Throwable) {
                showMessage(getString(R.string.retrieveFail))
                showLoading(false)
            }

        })
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

    private fun showMessage(message: String) {
        Toast.makeText(this ,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            activityGroupListBinding?.progressBar2?.visibility = View.VISIBLE
        }
        else {
            activityGroupListBinding?.progressBar2?.visibility = View.INVISIBLE
        }
    }

}