package com.c23ps076.mogerapp.screen.groupList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.data.MemberEmail
import kotlinx.android.synthetic.main.subitem_member.view.*

class GroupListChildAdapter(private val listMemberInfo: ArrayList<MemberEmail>)
    : RecyclerView.Adapter<GroupListChildAdapter.MemberViewHolder>(){

        inner class MemberViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            fun bind(member: MemberEmail) {
                with(itemView) {
                    tv_member.text = member.email
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subitem_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listMemberInfo.size
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(listMemberInfo[position])


    }
}