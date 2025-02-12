package com.exner.tools.activitytimerfortv.ui

import androidx.lifecycle.ViewModel
import com.exner.tools.activitytimerfortv.data.AllDataHolder
import com.exner.tools.activitytimerfortv.data.persistence.TimerDataRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ConnectedViewModel @Inject constructor(
    val repository: TimerDataRepository
) : ViewModel() {

    private val TAG = "CVM"

    private val moshi: Moshi = Moshi.Builder().build()
    val adapter: JsonAdapter<AllDataHolder> = moshi.adapter(AllDataHolder::class.java)

}