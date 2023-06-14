package com.c23ps076.mogerapp.screen.groupdashboard


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.data.TransactionInfo
import com.c23ps076.mogerapp.api.data.transaction
import kotlinx.android.synthetic.main.item_transaction_groupby_day.view.*

class TransactionParentAdapter(private val listTransactionInfo: ArrayList<TransactionInfo>)
    : RecyclerView.Adapter<TransactionParentAdapter.TransactionViewHolder>(){

        inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

            fun bind(transactionInfo: TransactionInfo) {
                with(itemView) {
                    tv_day.text = "Day "+ transactionInfo.day.toString()
                    if (transactionInfo.expense < 0){
                        tv_total_day_expense_value.text = "Rp." + (transactionInfo.expense * -1).toString()
                        tv_total_day_expense_value.setTextColor(ContextCompat.getColor(context, R.color.discord_red))
                    }
                    else if (transactionInfo.expense > 0){
                        tv_total_day_expense_value.text = "Rp." + transactionInfo.expense.toString()
                        tv_total_day_expense_value.setTextColor(ContextCompat.getColor(context, R.color.discord_green))
                    }
                    else {
                        tv_total_day_expense_value.text = "Rp." + transactionInfo.expense.toString()
                        tv_total_day_expense_value.setTextColor(ContextCompat.getColor(context, R.color.white))
                    }



                    rv_transaction.setHasFixedSize(true)
                    rv_transaction.layoutManager=LinearLayoutManager(itemView.context)
                    val transactions: ArrayList<transaction> = transactionInfo.transactions
                    val adapter = TransactionChildAdapter(transactions)
                    rv_transaction.adapter = adapter
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_groupby_day, parent, false)
        return TransactionViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listTransactionInfo.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(listTransactionInfo[position])
    }
}

