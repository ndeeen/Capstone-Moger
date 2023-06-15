package com.c23ps076.mogerapp.screen.addTransaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.WalletInfo
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.databinding.ActivityAddTransactionBinding
import com.c23ps076.mogerapp.screen.groupdashboard.GroupDashboardActivity
import kotlinx.android.synthetic.main.activity_add_transaction.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddTransactionActivity : AppCompatActivity() {
    private lateinit var activityAddTransactionBinding: ActivityAddTransactionBinding
    private var choosenButtonKind = "outcome"
    private lateinit var partyName: String
    private val listWallet = ArrayList<WalletInfo>()

    lateinit var userLogPreference: Preferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAddTransactionBinding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(activityAddTransactionBinding.root)

        partyName = intent.getStringExtra("PARTYNAME").toString()
        supportActionBar?.title = partyName
        supportActionBar?.show()
        userLogPreference = Preferences(this)

        activityAddTransactionBinding.apply {
            rvWalletAddtransaction.setHasFixedSize(true)
            rvWalletAddtransaction.layoutManager = LinearLayoutManager(this@AddTransactionActivity)
        }
        getWalletData()
        activityAddTransactionBinding.apply {
            tvSelectedKind.text = String.format(getString(R.string.selectedKind), choosenButtonKind)

            btnKindIncome.setOnClickListener {
                choosenButtonKind = "income"
                tvSelectedKind.text = String.format(getString(R.string.selectedKind), choosenButtonKind)
                etCategoryToWalletTransaction.hint = "Category"
            }
            btnKindOutcome.setOnClickListener {
                choosenButtonKind = "outcome"
                tvSelectedKind.text = String.format(getString(R.string.selectedKind), choosenButtonKind)
                etCategoryToWalletTransaction.hint = "Category"
            }
            btnKindTransfer.setOnClickListener {
                choosenButtonKind = "transfer"
                tvSelectedKind.text = String.format(getString(R.string.selectedKind), choosenButtonKind)
                etCategoryToWalletTransaction.hint = "To Wallet"
            }
            btnAddTransaction.setOnClickListener {
                addTransaction()
            }
        }
    }

    fun getWalletData() {
        val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
        service.getWallet(partyName)
            .enqueue(object: Callback<ArrayList<WalletInfo>> {
                override fun onResponse(
                    call: Call<ArrayList<WalletInfo>>,
                    response: Response<ArrayList<WalletInfo>>
                ) {
                    Log.e("walletdata", response.body().toString())
                    response.body()?.let {
                        listWallet.addAll(it)
                    }
                    val adapter = WalletTransactionAdapter(listWallet)
                    rv_wallet_addtransaction.adapter = adapter
                }

                override fun onFailure(call: Call<ArrayList<WalletInfo>>, t: Throwable) {
                    Log.e("", "failed retrive wallet data")
                }

            })
    }

    fun addTransaction() {


        val createdBy = userLogPreference.getLoginData().email
        val kind = choosenButtonKind
        val fromWallet = et_wallet_transaction.text
        var toWallet: String
        var incomeOutcomeKind: String
        if (kind == "income" || kind == "outcome"){
            toWallet = "null"
            incomeOutcomeKind = et_category_to_wallet_transaction.text.toString()
        }
        else {
            toWallet = et_category_to_wallet_transaction.text.toString()
            incomeOutcomeKind = "null"
        }
        val amount = et_amount_transaction.text
        val stamp = et_date_transaction.text

        if (stamp != null && fromWallet != null && et_category_to_wallet_transaction.text != null && amount != null) {
            val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
            service.addTransaction(partyName,createdBy,kind, fromWallet.toString(), toWallet, amount.toString(), stamp.toString(), incomeOutcomeKind)
                .enqueue(object: Callback<Int> {
                    override fun onResponse(call: Call<Int>, response: Response<Int>) {
                        Log.e("msg post transaksi", "berhasil")
                    }

                    override fun onFailure(call: Call<Int>, t: Throwable) {
                        Log.e("msg post transaksi", "gagal")
                    }

                })
        }

        val intentBack = Intent(this, GroupDashboardActivity::class.java)
        intentBack.apply {
            putExtra("PARTYNAME", partyName)
        }
        startActivity(intentBack)
        this.finish()
    }
}
