package com.c23ps076.mogerapp.screen.groupdashboard

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.c23ps076.mogerapp.R
import com.c23ps076.mogerapp.api.data.IncomeOutcome
import com.c23ps076.mogerapp.api.data.TransactionInfo
import com.c23ps076.mogerapp.api.utils.Preferences
import com.c23ps076.mogerapp.api.utils.RetroLazy
import com.c23ps076.mogerapp.databinding.ActivityGroupDashboardBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityGroupDashboardBinding = ActivityGroupDashboardBinding.inflate(layoutInflater)
        setContentView(activityGroupDashboardBinding?.root)

        partyName = intent.getStringExtra("PARTYNAME").toString()
        supportActionBar?.setTitle(partyName)
        supportActionBar?.show()

        activityGroupDashboardBinding?.apply {
            btnPreviousMonth.setOnClickListener{
                previousNextMonthYear(-1)
            }
            btnNextMonth.setOnClickListener {
                previousNextMonthYear(1)
            }
            rv_transactions.setHasFixedSize(true)
            rv_transactions.layoutManager = LinearLayoutManager(this@GroupDashboardActivity)
        }
        changeSelectedMonthYear()
    }

    fun getTransactionData() {
        RetroLazy.instance.getTransaction(partyName, month.toString(),year.toString())
            .enqueue(object : Callback<ArrayList<TransactionInfo>>{
                override fun onResponse(
                    call: Call<ArrayList<TransactionInfo>>,
                    response: Response<ArrayList<TransactionInfo>>
                ) {
                    if (response.body()?.size != 0) {
                        response.body()?.let {
                            listTransactionInfo.clear()
                            listTransactionInfo.addAll(it)
                        }
                        val adapter = TransactionParentAdapter(listTransactionInfo)
                        rv_transactions.adapter = adapter
                    }
                    else {
                        Log.e("rv failed", "rv failed")
                    }
                }

                override fun onFailure(call: Call<ArrayList<TransactionInfo>>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
    fun getIncomeOutcome() {
        if (partyName != null) {
            RetroLazy.instance.getIncomeOutcome(partyName, month.toString(), year.toString())
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