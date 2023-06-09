package com.c23ps076.mogerapp.screen.membermanagement

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.Member
import com.c23ps076.mogerapp.api.data.MemberRequest
import com.c23ps076.mogerapp.api.data.MessageResponse
import kotlinx.android.synthetic.main.item_member_email.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberManagementAdapter(private val listMember: ArrayList<Member>, private val partyName: String)
    : RecyclerView.Adapter<MemberManagementAdapter.MemberHolder>(){

        inner class MemberHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            fun bind(member: Member){
                with(itemView) {
                    tv_email_member.text = member.email.toString()

                    btn_delete_member.setOnClickListener {
                        deleteMember(member.email.toString(), partyName)
                    }
                }
            }
        }

    private fun deleteMember(email: String, partyName: String){
        if (email != null && partyName != null) {
            val request = MemberRequest(partyName, email)
            val service = ApiService.create(BASE_URL)
            service.deleteMember(request)
                .enqueue(object : Callback<MessageResponse> {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                        listMember.removeIf {
                            it.email == email
                        }
                        notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        Log.e("", "Failed Delete Member")
                    }

                })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member_email, parent, false)
        return MemberHolder(view)
    }

    override fun getItemCount(): Int {
        return listMember.size
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.bind(listMember[position])
    }
}