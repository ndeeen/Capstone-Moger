package com.c23ps076.mogerapp.screen.membermanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.Member
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.databinding.ActivityMemberManagementBinding
import kotlinx.android.synthetic.main.activity_member_management.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberManagementActivity : AppCompatActivity() {
    private lateinit var activityMemberManagementBinding: ActivityMemberManagementBinding
    private lateinit var userLogPreferences: Preferences
    private lateinit var partyName: String
    private var listMember = ArrayList<Member>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMemberManagementBinding = ActivityMemberManagementBinding.inflate(layoutInflater)
        setContentView(activityMemberManagementBinding.root)
        userLogPreferences = Preferences(this)

        partyName = intent.getStringExtra("PARTYNAME").toString()
        supportActionBar?.title = partyName
        supportActionBar?.show()

        activityMemberManagementBinding?.apply {
            rv_email_member.setHasFixedSize(true)
            rv_email_member.layoutManager = LinearLayoutManager(this@MemberManagementActivity)
        }
        getMemberList()

        activityMemberManagementBinding?.apply {
            btnAddMember.setOnClickListener{
                addMember()
            }
        }
    }

    fun getMemberList() {
        val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
        service.getMemberLists(partyName)
            .enqueue(object : Callback<ArrayList<Member>>{
                override fun onResponse(
                    call: Call<ArrayList<Member>>,
                    response: Response<ArrayList<Member>>
                ) {
                    response.body()?.let {
                        listMember.clear()
                        listMember.addAll(it)
                    }
                    val adapter = MemberManagementAdapter(listMember, partyName)
                    rv_email_member.adapter = adapter
                }

                override fun onFailure(call: Call<ArrayList<Member>>, t: Throwable) {
                    t.message?.let { Log.e("", it) }
                }

            })
    }

    fun addMember() {
        val email = activityMemberManagementBinding?.etMemberInput?.text?.toString()?.trim()
        if (email != null && partyName != null) {
            val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
            service.addMember(email, partyName)
                .enqueue(object : Callback<Int> {
                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        Log.e("", "Member Added")
                        refresh()
                    }

                    override fun onFailure(call: Call<Int>, t: Throwable) {
                        Log.e("", "Failed Member Add")
                    }

                })
        }
    }

    fun refresh() {
        this.recreate()
    }
}