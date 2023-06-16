package com.c23ps076.mogerapp.screen.walletManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.BalanceInfo
import com.c23ps076.mogerapp.api.data.MessageResponse
import com.c23ps076.mogerapp.api.data.WalletInfo
import com.c23ps076.mogerapp.api.data.WalletRequest
import com.c23ps076.mogerapp.databinding.ActivityWalletManagementBinding
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
        supportActionBar?.title = String.format(getString(R.string.barWalletManagement), partyName)
        supportActionBar?.show()
        activityWalletManagementBinding?.apply {
            rv_wallet_management.setHasFixedSize(true)
            rv_wallet_management.layoutManager = LinearLayoutManager(this@WalletManagementActivity)
        }
        initialize()
        activityWalletManagementBinding?.apply {
            btnAddWallet.setOnClickListener {
                showLoading(true)
                addWallet()
                refresh()
            }
        }
    }

    private fun initialize() {
        showLoading(true)
        getBalance()
        getWalletData()
    }

    private fun getBalance() {
        val service = ApiService.create(BASE_URL)
        service.getBalance(partyName)
            .enqueue(object: Callback<ArrayList<BalanceInfo>>{
                override fun onResponse(
                    call: Call<ArrayList<BalanceInfo>>,
                    response: Response<ArrayList<BalanceInfo>>
                ) {
                    tv_total_balance.text = String.format(getString(R.string.moneyValue), response?.body()?.get(0)?.balance.toString())
                }

                override fun onFailure(call: Call<ArrayList<BalanceInfo>>, t: Throwable) {
                    showMessage(getString(R.string.retrieveFail))
                }

            })
    }

    private fun getWalletData() {
        val service = ApiService.create(BASE_URL)
        service.getWallet(partyName)
            .enqueue(object: Callback<ArrayList<WalletInfo>> {
                override fun onResponse(
                    call: Call<ArrayList<WalletInfo>>,
                    response: Response<ArrayList<WalletInfo>>
                ) {
                    response.body()?.let {
                        listWallet.clear()
                        listWallet.addAll(it)
                    }
                    val adapter = WalletManagementAdapter(listWallet, partyName)
                    rv_wallet_management.adapter = adapter
                    showLoading(false)
                }

                override fun onFailure(call: Call<ArrayList<WalletInfo>>, t: Throwable) {
                    showMessage(getString(R.string.retrieveFail))
                    showLoading(false)
                }

            })
    }

    private fun addWallet() {
        val walletName = et_wallet_name.text.toString()
        val balance = Integer.parseInt(et_balance.text.toString())

        if (walletName != null && balance != null) {
            val service = ApiService.create(BASE_URL)
            val requestBody = WalletRequest(partyName, walletName, balance)
            service.addWallet(requestBody)
                .enqueue(object : Callback<MessageResponse> {
                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        showMessage(getString(R.string.walletadded))
                        showLoading(false)
                    }

                    override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                        showMessage(getString(R.string.walletaddFailed))
                        showLoading(false)
                    }

                })
        }

    }

    private fun showMessage(message: String) {
        Toast.makeText(this ,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            activityWalletManagementBinding?.progressBar4?.visibility = View.VISIBLE
        }
        else {
            activityWalletManagementBinding?.progressBar4?.visibility = View.INVISIBLE
        }
    }

    private fun refresh() {
        this.recreate()
    }
}