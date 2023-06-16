package com.c23ps076.mogerapp.screen.addTransaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.BuildConfig.BASE_URL
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.MessageResponse
import com.c23ps076.mogerapp.api.data.TransactionRequest
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
        supportActionBar?.title = String.format(getString(R.string.barAddTransaction), partyName)
        supportActionBar?.show()
        userLogPreference = Preferences(this)

        activityAddTransactionBinding.apply {
            rvWalletAddtransaction.setHasFixedSize(true)
            rvWalletAddtransaction.layoutManager = LinearLayoutManager(this@AddTransactionActivity)
        }
        showLoading(true)
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
                showLoading(true)
                addTransaction()
            }
        }
    }

    fun getWalletData() {
        val service = ApiService.create(BASE_URL)
        service.getWallet(partyName)
            .enqueue(object: Callback<ArrayList<WalletInfo>> {
                override fun onResponse(
                    call: Call<ArrayList<WalletInfo>>,
                    response: Response<ArrayList<WalletInfo>>
                ) {
                    response.body()?.let {
                        listWallet.addAll(it)
                    }
                    val adapter = WalletTransactionAdapter(listWallet)
                    rv_wallet_addtransaction.adapter = adapter
                    showLoading(false)
                }

                override fun onFailure(call: Call<ArrayList<WalletInfo>>, t: Throwable) {
                    showMessage(getString(R.string.retrieveFail))
                    showLoading(false)
                }

            })
    }

    fun addTransaction() {


        val createdBy = userLogPreference.getLoginData().email
        val kind = choosenButtonKind
        val fromWallet = et_wallet_transaction.text
        var toWallet: String?
        var incomeOutcomeKind: String?
        if (kind == "income" || kind == "outcome"){
            toWallet = null
            incomeOutcomeKind = et_category_to_wallet_transaction.text.toString()
        }
        else {
            toWallet = et_category_to_wallet_transaction.text.toString()
            incomeOutcomeKind = null
        }
        val amount = Integer.parseInt(et_amount_transaction.text.toString())
        val stamp = String.format(getString(R.string.formatDate), et_date_transaction.text.toString())

        if (stamp != null && fromWallet != null && et_category_to_wallet_transaction.text != null && amount != null) {
            val request = TransactionRequest(partyName,createdBy,kind, fromWallet.toString(), toWallet, amount, stamp.toString(), incomeOutcomeKind)
            val service = ApiService.create(BASE_URL)
            service.addTransaction(request)
                .enqueue(object: Callback<MessageResponse> {
                    override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                        showMessage(getString(R.string.transactionadded))
                        showLoading(false)
                        backToPrevAct()
                    }

                    override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                        showMessage(getString(R.string.transactionFailed))
                        showLoading(false)
                    }

                })
        }


    }

    private fun backToPrevAct() {
        val intentBack = Intent(this, GroupDashboardActivity::class.java)
        intentBack.apply {
            putExtra("PARTYNAME", partyName)
        }
        startActivity(intentBack)
        this.finish()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this ,message, Toast.LENGTH_LONG).show()
    }

    private fun showLoading(status: Boolean) {
        if (status) {
            activityAddTransactionBinding?.progressBar5?.visibility = View.VISIBLE
        }
        else {
            activityAddTransactionBinding?.progressBar5?.visibility = View.INVISIBLE
        }
    }
}
