package com.c23ps076.mogerapp.screen.walletManagement


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_wallet_management.view.*
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.data.WalletInfo

class WalletManagementAdapter(private val listWallet: ArrayList<WalletInfo>)
    :RecyclerView.Adapter<WalletManagementAdapter.WalletHolder>(){

    inner class WalletHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(wallet: WalletInfo) {
            with(itemView) {
                tv_walletname_item.text = wallet.walletName.toString()
                tv_balance_item.text = "Rp."+wallet.balance.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_wallet_management, parent, false)
        return WalletHolder(view)
    }

    override fun getItemCount(): Int {
        return listWallet.size
    }

    override fun onBindViewHolder(holder: WalletHolder, position: Int) {
        holder.bind(listWallet[position])

    }
}