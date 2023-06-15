package com.c23ps076.mogerapp.screen.groupdashboard

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.data.IncomeOutcome
import com.c23ps076.mogerapp.api.data.TransactionInfo
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.api.utils.RetroLazy
import com.c23ps076.mogerapp.databinding.ActivityGroupDashboardBinding
import com.c23ps076.mogerapp.screen.addTransaction.AddTransactionActivity
import com.c23ps076.mogerapp.screen.membermanagement.MemberManagementActivity
import com.c23ps076.mogerapp.screen.walletManagement.WalletManagementActivity
import kotlinx.android.synthetic.main.activity_group_dashboard.*
import kotlinx.android.synthetic.main.item_transaction_groupby_day.*
import retrofit2.Call
import java.time.LocalDate
import java.util.Calendar
import retrofit2.Callback
import retrofit2.Response

class GroupDashboardActivity : AppCompatActivity() {
    private  var activityGroupDashboardBinding: ActivityGroupDashboardBinding? = null
    private lateinit var userLogPreferences: Preferences
    val calendar = Calendar.getInstance()
    var month = calendar.get(Calendar.MONTH)
    var year = calendar.get(Calendar.YEAR)
    lateinit var partyName: String
    private val listTransactionInfo = ArrayList<TransactionInfo>()

    private val rotateOpen: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim) }
    private val rotateClose: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim) }
    private val fromBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim) }
    private val toBottom: Animation by lazy { AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim) }

    private var clickedButton = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGroupDashboardBinding = ActivityGroupDashboardBinding.inflate(layoutInflater)
        setContentView(activityGroupDashboardBinding?.root)

        partyName = intent.getStringExtra("PARTYNAME").toString()
        supportActionBar?.setTitle(partyName)
        supportActionBar?.show()

        btn_expands.setOnClickListener {
            addExpandButton()
        }

        activityGroupDashboardBinding?.apply {
            btnPreviousMonth.setOnClickListener{
                previousNextMonthYear(-1)
            }
            btnNextMonth.setOnClickListener {
                previousNextMonthYear(1)
            }
            btnManageMember.setOnClickListener {
                val intentMember = Intent(this@GroupDashboardActivity, MemberManagementActivity::class.java)
                intentMember.apply {
                    putExtra("PARTYNAME", partyName)
                }
                startActivity(intentMember)
            }
            btnAddTransaction.setOnClickListener {
                val intentMember = Intent(this@GroupDashboardActivity, AddTransactionActivity::class.java)
                intentMember.apply {
                    putExtra("PARTYNAME", partyName)
                }
                startActivity(intentMember)
            }
            btnManageWallet.setOnClickListener {
                val intentMember = Intent(this@GroupDashboardActivity, WalletManagementActivity::class.java)
                intentMember.apply {
                    putExtra("PARTYNAME", partyName)
                }
                startActivity(intentMember)
            }
            rv_transactions.setHasFixedSize(true)
            rv_transactions.layoutManager = LinearLayoutManager(this@GroupDashboardActivity)


        }
        changeSelectedMonthYear()
    }

    private fun addExpandButton() {
        setVisibility(clickedButton)
        setAnimation(clickedButton)
        clickedButton = !clickedButton
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            btn_add_transaction.visibility = View.VISIBLE
            btn_manage_wallet.visibility = View.VISIBLE
            btn_manage_member.visibility = View.VISIBLE
        }
        else {
            btn_add_transaction.visibility = View.INVISIBLE
            btn_manage_wallet.visibility = View.INVISIBLE
            btn_manage_member.visibility = View.INVISIBLE
        }
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            btn_expands.startAnimation(rotateOpen)
            btn_add_transaction.startAnimation(fromBottom)
            btn_manage_wallet.startAnimation(fromBottom)
            btn_manage_member.startAnimation(fromBottom)
        }
        else {
            btn_expands.startAnimation(rotateClose)
            btn_add_transaction.startAnimation(toBottom)
            btn_manage_wallet.startAnimation(toBottom)
            btn_manage_member.startAnimation(toBottom)
        }
    }

    fun getTransactionData() {
        val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
        service.getTransaction(partyName, month.toString(),year.toString())
            .enqueue(object : Callback<ArrayList<TransactionInfo>>{
                override fun onResponse(
                    call: Call<ArrayList<TransactionInfo>>,
                    response: Response<ArrayList<TransactionInfo>>
                ) {
                        response.body()?.let {
                            listTransactionInfo.clear()
                            listTransactionInfo.addAll(it)
                        }
                        val adapter = TransactionParentAdapter(listTransactionInfo)
                        rv_transactions.adapter = adapter

                }

                override fun onFailure(call: Call<ArrayList<TransactionInfo>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
    fun getIncomeOutcome() {
        if (partyName != null) {
            val service = ApiService.create("http://www.wakacipuy.my.id/dokuApp/")
            service.getIncomeOutcome(partyName, month.toString(), year.toString())
                .enqueue(object : Callback<IncomeOutcome> {
                    override fun onResponse(
                        call: Call<IncomeOutcome>,
                        response: Response<IncomeOutcome>
                    ) {
                        Log.e("Income Outcome", response.body().toString())
                        response.body()?.let {
                            activityGroupDashboardBinding?.apply {
                                tvIncomeValue.text = String.format(getString(R.string.moneyValue), response.body()?.income.toString())
                                tvOutcomeValue.text = String.format(getString(R.string.moneyValue), response.body()?.outcome.toString())
                            }
                        }
                    }

                    override fun onFailure(call: Call<IncomeOutcome>, t: Throwable) {
                        Log.e("Income Outcome", "gagal")
                    }

                })
        }
    }
    private fun changeSelectedMonthYear() {
        activityGroupDashboardBinding?.apply {
            tvSelectedMonthAndYear.text = String.format(getString(R.string.selectedMonth), changeIntMonthToText(month), year.toString())

        }
        getIncomeOutcome()
        getTransactionData()
    }
    private fun changeIntMonthToText(monthIdx: Int): String {
        val arrMonth = arrayOf(
            "January",
            "February",
            "March",
            "April",
            "May",
            "June",
            "July",
            "August",
            "September",
            "October",
            "November",
            "December"
        )

        return arrMonth.get(monthIdx)
    }

    private fun previousNextMonthYear(number: Int) {
        if (number == 1) {
            if (month == 11) {
                month = 0
                year += 1
            }
            else{
                month += 1
            }
        }
        else if (number == -1) {
            if (month == 0) {
                month = 11
                year -= 1
            }
            else {
                month -= 1
            }
        }
        changeSelectedMonthYear()
    }

    override fun onDestroy() {
        super.onDestroy()
        activityGroupDashboardBinding = null
    }
}