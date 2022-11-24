package com.example.artun_cimensel_myruns4.services

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapServiceViewModel : ViewModel(), ServiceConnection {

    override fun onServiceConnected(name: ComponentName, iBinder: IBinder) {
        println("debug: ViewModel: onServiceConnected() called; ComponentName: $name")
        val tempBinder = iBinder as MapLocationService.MyBinder
    }

    override fun onServiceDisconnected(name: ComponentName) {
        println("debug: Activity: onServiceDisconnected() called")
    }
}