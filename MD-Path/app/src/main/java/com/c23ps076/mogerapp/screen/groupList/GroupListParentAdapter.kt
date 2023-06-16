package com.c23ps076.mogerapp.screen.groupList

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.data.GroupInfo
import com.c23ps076.mogerapp.api.data.MemberEmail
import com.c23ps076.mogerapp.screen.groupdashboard.GroupDashboardActivity
import kotlinx.android.synthetic.main.item_group.view.*

class GroupListParentAdapter(private val listGroupInfo: ArrayList<GroupInfo>)
    : RecyclerView.Adapter<GroupListParentAdapter.GroupInfoViewHolder>() {


        inner class GroupInfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            val rvChild: RecyclerView = itemView.findViewById(R.id.rv_member)
            fun bind(groupInfo: GroupInfo) {
                with(itemView) {
                    tv_groupname.text = groupInfo.partyName
                    tv_groupbalance.text = "Rp."+groupInfo.ballance[0].ballance.toString()

                    rvChild.setHasFixedSize(true)
                    rvChild.layoutManager = LinearLayoutManager(itemView.context)
                    val listMember: ArrayList<MemberEmail> = groupInfo.members
                    val adapter = GroupListChildAdapter(listMember)
                    rvChild.adapter = adapter

                    btn_entergroup.setOnClickListener{
                        val intent = Intent(itemView.context, GroupDashboardActivity::class.java)
                        intent.apply {
                            putExtra("PARTYNAME", groupInfo.partyName)
                        }
                        itemView.context.startActivity(intent)
                    }
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupInfoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupInfoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listGroupInfo.size
    }

    override fun onBindViewHolder(holder: GroupInfoViewHolder, position: Int) {
        holder.bind(listGroupInfo[position])
    }
}