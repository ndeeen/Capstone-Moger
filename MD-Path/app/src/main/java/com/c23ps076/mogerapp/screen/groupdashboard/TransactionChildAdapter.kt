package com.c23ps076.mogerapp.screen.groupdashboard

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.MessageResponse
import com.c23ps076.mogerapp.api.data.transaction
import kotlinx.android.synthetic.main.subitem_transaction.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionChildAdapter(private val transactions: ArrayList<transaction>)
    : RecyclerView.Adapter<TransactionChildAdapter.ChildTransactionViewHolder>(){

        inner class ChildTransactionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
            fun bind(trk: transaction) {
                with(itemView) {
                    tv_kind.text = trk.kind
                    tv_from.text = trk.from
                    tv_category.text = trk.category
                    tv_created_by.text = trk.createdBy
                    tv_amount.text = "Rp."+ trk.amount.toString()
                    if (trk.kind == "outcome") {
                        tv_amount.setTextColor(ContextCompat.getColor(context, R.color.discord_red))
                    }
                    else if (trk.kind == "income") {
                        tv_amount.setTextColor(ContextCompat.getColor(context, R.color.discord_green))
                    }
                    else {
                        tv_amount.setTextColor(ContextCompat.getColor(context, R.color.white))
                        tv_category.text= "tf to "+trk.toWallet
                    }

                    btn_delete_transaction.setOnClickListener {
                        deleteTransaction(trk.id)
                    }
                }
            }
        }

    private fun deleteTransaction(id: Int) {
        val service = ApiService.create(BASE_URL)
        service.deleteTransaction(id).enqueue(object : Callback<MessageResponse>{
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(
                call: Call<MessageResponse>,
                response: Response<MessageResponse>
            ) {
                transactions.removeIf {
                    it.id == id
                }
                notifyDataSetChanged()
            }

            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("", "delete transaction failed")
            }

        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildTransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subitem_transaction, parent, false)
        return ChildTransactionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: ChildTransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }
}