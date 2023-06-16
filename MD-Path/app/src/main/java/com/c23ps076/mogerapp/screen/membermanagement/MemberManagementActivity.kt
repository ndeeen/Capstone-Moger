package com.c23ps076.mogerapp.screen.membermanagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.Member
import com.c23ps076.mogerapp.api.data.MemberRequest
import com.c23ps076.mogerapp.api.data.MessageResponse
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
        supportActionBar?.title = String.format(getString(R.string.barMemberManagement), partyName)
        supportActionBar?.show()

        activityMemberManagementBinding?.apply {
            rv_email_member.setHasFixedSize(true)
            rv_email_member.layoutManager = LinearLayoutManager(this@MemberManagementActivity)
        }

        showLoading(true)
        getMemberList()

        activityMemberManagementBinding?.apply {
            btnAddMember.setOnClickListener{
                showLoading(true)
                addMember()
            }
        }
    }

    fun getMemberList() {
        val service = ApiService.create(BASE_URL)
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
                    showLoading(false)
                }

                override fun onFailure(call: Call<ArrayList<Member>>, t: Throwable) {
                    showMessage(getString(R.string.retrieveFail))
                    showLoading(false)
                }

            })
    }

    fun addMember() {
        val email = activityMemberManagementBinding?.etMemberInput?.text?.toString()
        if (email != null && partyName != null) {
            val request = MemberRequest(partyName, email)
            val service = ApiService.create(BASE_URL)
            service.addMember(request)
                .enqueue(object : Callback<MessageResponse> {
                    override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                        showMessage(getString(R.string.memberadded))
                        showLoading(false)
                        refresh()
                    }

                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        showMessage(getString(R.string.memberaddFaied))
                        showLoading(false)
                    }

                })
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this ,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            activityMemberManagementBinding?.progressBar6?.visibility = View.VISIBLE
        }
        else {
            activityMemberManagementBinding?.progressBar6?.visibility = View.INVISIBLE
        }
    }
    fun refresh() {
        this.recreate()
    }
}