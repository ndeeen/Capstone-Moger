package com.c23ps076.mogerapp.screen.walletManagement

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import kotlinx.android.synthetic.main.item_wallet_management.view.*
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.MessageResponse
import com.c23ps076.mogerapp.api.data.WalletInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletManagementAdapter(private val listWallet: ArrayList<WalletInfo>, private val partyName: String)
    :RecyclerView.Adapter<WalletManagementAdapter.WalletHolder>(){
    inner class WalletHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        fun bind(wallet: WalletInfo) {
            with(itemView) {
                tv_walletname_item.text = wallet.walletName.toString()
                tv_balance_item.text = "Rp."+wallet.balance.toString()

                btn_delete_wallet.setOnClickListener {
                    deleteWallet(partyName, wallet.walletName.toString())
                }
            }
        }
    }

    private fun deleteWallet(partyName: String, walletName: String){
        val service = ApiService.create(BASE_URL)
        service.deleteWallet(partyName, walletName).enqueue(object: Callback<MessageResponse> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                listWallet.removeIf {
                    it.walletName == walletName
                }
                notifyDataSetChanged()
            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                Log.e("", "delete wallet failed")
            }

        })
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