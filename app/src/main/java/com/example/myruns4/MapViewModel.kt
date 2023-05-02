package com.example.myruns4

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

//receives message from TrackingService
class MapViewModel : ViewModel(), ServiceConnection {


    private var _bundle = MutableLiveData<Bundle>()
    private var _automatic_activity = MutableLiveData<String>()

    val bundle: LiveData<Bundle>
        get() = _bundle


    val automatic_activity: LiveData<String>
        get() = _automatic_activity


    private lateinit var messHandler: MyMessageHandler
    init {
        messHandler = MyMessageHandler(Looper.getMainLooper())
    }



    override fun onServiceConnected(compName: ComponentName?, iBinder: IBinder?) {
        //TODO("Not yet implemented")
        Log.d("MapViewModel", "onServiceConnected")
        val trackingBinder = iBinder as TrackingService.MyBinder
        trackingBinder.setmsgHandler(messHandler)
    }

    override fun onServiceDisconnected(compName: ComponentName?) {
        //TODO("Not yet implemented")
        Log.d("MapViewModel", "onServiceDisconnected")

    }

    inner class MyMessageHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(mess: Message) {
            if (mess.what == TrackingService.MSG) {
                _bundle.value = mess.data
            }
            else if (mess.what == TrackingService.AUTO_MSG)
            {
                _automatic_activity.value = mess.data.getString("AUTO_ACTIVITY")
            }
        }
    }
}