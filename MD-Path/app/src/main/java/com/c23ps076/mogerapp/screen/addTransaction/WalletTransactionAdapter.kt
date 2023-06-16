package com.c23ps076.mogerapp.screen.addTransaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_transaction_wallet.view.*
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.data.WalletInfo
import com.c23ps076.mogerapp.databinding.ActivityAddTransactionBinding

class WalletTransactionAdapter(private val listWallet: ArrayList<WalletInfo>)
    :RecyclerView.Adapter<WalletTransactionAdapter.WalletHolder>(){

        inner class WalletHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            fun bind(wallet: WalletInfo) {
                with(itemView) {
                    tv_walletname.text = wallet.walletName.toString()
                }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_wallet, parent, false)
        return WalletHolder(view)
    }

    override fun getItemCount(): Int {
        return listWallet.size
    }

    override fun onBindViewHolder(holder: WalletHolder, position: Int) {
        holder.bind(listWallet[position])

    }
}