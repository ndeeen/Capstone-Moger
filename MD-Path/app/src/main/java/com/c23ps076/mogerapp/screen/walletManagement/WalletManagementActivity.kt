package com.c23ps076.mogerapp.screen.walletManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.BalanceInfo
import com.c23ps076.mogerapp.api.data.WalletInfo
import com.c23ps076.mogerapp.databinding.ActivityMemberManagementBinding
import com.c23ps076.mogerapp.databinding.ActivityWalletManagementBinding
import com.c23ps076.mogerapp.screen.addTransaction.WalletTransactionAdapter
import kotlinx.android.synthetic.main.activity_add_transaction.*
import kotlinx.android.synthetic.main.activity_wallet_management.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletManagementActivity : AppCompatActivity() {
    private lateinit var activityWalletManagementBinding: ActivityWalletManagementBinding
    private lateinit var partyName: String
    private val listWallet = ArrayList<WalletInfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityWalletManagementBinding = ActivityWalletManagementBinding.inflate(layoutInflater)
        setContentView(activityWalletManagementBinding.root)

        partyName = intent.getStringExtra("PARTYNAME").toString()
        supportActionBar?.title = partyName
        supportActionBar?.show()
        activityWalletManagementBinding?.apply {
            rv_wallet_management.setHasFixedSize(true)
            rv_wallet_management.layoutManager = LinearLayoutManager(this@WalletManagementActivity)
        }
        initialize()
        activityWalletManagementBinding?.apply {
            btnAddWallet.setOnClickListener {
                addWallet()
                refresh()
            }
        }
    }

    private fun initialize() {
        getBalance()
        getWalletData()
    }

    private fun getBalance() {
        val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
        service.getBalance(partyName)
            .enqueue(object: Callback<ArrayList<BalanceInfo>>{
                override fun onResponse(
                    call: Call<ArrayList<BalanceInfo>>,
                    response: Response<ArrayList<BalanceInfo>>
                ) {
                    tv_total_balance.text = String.format(getString(R.string.moneyValue), response?.body()?.get(0)?.ballance.toString())
                }

                override fun onFailure(call: Call<ArrayList<BalanceInfo>>, t: Throwable) {
                    Log.e("", "Retrieve balance failed")
                }

            })
    }

    private fun getWalletData() {
        val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
        service.getWallet(partyName)
            .enqueue(object: Callback<ArrayList<WalletInfo>> {
                override fun onResponse(
                    call: Call<ArrayList<WalletInfo>>,
                    response: Response<ArrayList<WalletInfo>>
                ) {
                    Log.e("walletdata", response.body().toString())
                    response.body()?.let {
                        listWallet.clear()
                        listWallet.addAll(it)
                    }
                    val adapter = WalletManagementAdapter(listWallet)
                    rv_wallet_management.adapter = adapter
                }

                override fun onFailure(call: Call<ArrayList<WalletInfo>>, t: Throwable) {
                    Log.e("", "failed retrive wallet data")
                }

            })
    }

    private fun addWallet() {
        val walletName = et_wallet_name.text.toString()
        val balance = et_balance.text.toString()

        if (walletName != null && balance != null) {
            val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
            service.addWallet(partyName, walletName, balance)
                .enqueue(object : Callback<Int> {
                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        Log.e("", "add wallet success")
                    }

                    override fun onFailure(call: Call<Int>, t: Throwable) {
                        Log.e("", "add wallet failed")
                    }

                })
        }

    }

    private fun refresh() {
        this.recreate()
    }
}