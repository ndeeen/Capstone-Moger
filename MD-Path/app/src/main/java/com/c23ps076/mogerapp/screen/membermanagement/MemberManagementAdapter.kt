package com.c23ps076.mogerapp.screen.membermanagement

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.Member
import kotlinx.android.synthetic.main.item_member_email.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MemberManagementAdapter(private val listMember: ArrayList<Member>, private val partyName: String, )
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
            val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
            service.deleteMember(email, partyName)
                .enqueue(object : Callback<Int> {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        Log.e("", "Member Deleted")
                        listMember.removeIf {
                            it.email == email
                        }
                        notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<Int>, t: Throwable) {
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