package com.c23ps076.mogerapp.screen.groupList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.data.GroupInfo
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.api.utils.RetroLazy
import com.c23ps076.mogerapp.databinding.ActivityGroupListBinding
import com.c23ps076.mogerapp.screen.profile.ProfileActivity
import kotlinx.android.synthetic.main.activity_group_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupListActivity : AppCompatActivity() {
    private lateinit var activityGroupListBinding: ActivityGroupListBinding
    lateinit var userLogPreferences: Preferences
//    private lateinit var groupListViewModel: GroupListViewModel
    private val listGroup = ArrayList<GroupInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGroupListBinding = ActivityGroupListBinding.inflate(layoutInflater)
        setContentView(activityGroupListBinding.root)
        supportActionBar?.title = getString(R.string.grouplist)
        supportActionBar?.show()

//        groupListViewModel = ViewModelProvider(this)[GroupListViewModel::class.java]


        userLogPreferences = Preferences(this)
        Log.e("prefGroupList", userLogPreferences.getLoginData().toString())
        getGroupList(userLogPreferences.getLoginData().email)

//        groupListViewModel.getGroupList(userLogPreferences.getLoginData().email.toString())
//        Log.e("liat message", groupListViewModel.message)
        initialize()
    }
    private fun initialize() {
        activityGroupListBinding.apply {
            rvGrouplist.setHasFixedSize(true)
            rvGrouplist.layoutManager = LinearLayoutManager(this@GroupListActivity)
        }
    }

    fun getGroupList(email: String) {
        RetroLazy.instance.getPartyList(email)
            .enqueue(object : Callback<ArrayList<GroupInfo>> {
                override fun onResponse(
                    call: Call<ArrayList<GroupInfo>>,
                    response: Response<ArrayList<GroupInfo>>
                ) {
                    Log.e("liat response", response.body().toString())
                    response.body()?.let {
                        listGroup.addAll(it)
                    }
                    val adapter = GroupListParentAdapter(listGroup)
                    rv_grouplist.adapter = adapter
                    Log.e("message","Data retrieved")
                }

                override fun onFailure(call: Call<ArrayList<GroupInfo>>, t: Throwable) {
                    Log.e("message","Retrieve Failed")
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
}