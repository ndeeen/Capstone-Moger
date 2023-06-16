package com.c23ps076.mogerapp.screen.groupList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.c23ps076.mogerapp.api.ApiService
import com.c23ps076.mogerapp.api.Retro
import com.c23ps076.mogerapp.api.data.GroupInfo
import com.c23ps076.mogerapp.api.utils.RetroLazy
import org.json.JSONArray
import org.json.JSONTokener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupListViewModel: ViewModel() {
    public var listGroup = ArrayList<GroupInfo>()

    var message = String()

    fun getGroupList(email: String) {
        RetroLazy.instance.getPartyList(email)
            .enqueue(object : Callback<ArrayList<GroupInfo>> {
                override fun onResponse(
                    call: Call<ArrayList<GroupInfo>>,
                    response: Response<ArrayList<GroupInfo>>
                ) {
                    response.body()?.let {
                        listGroup.addAll(it)
                    }
                    message = "Data retrieved"
                }

                override fun onFailure(call: Call<ArrayList<GroupInfo>>, t: Throwable) {
                    message = "Retrieve Failed"
                }

            })
    }
}