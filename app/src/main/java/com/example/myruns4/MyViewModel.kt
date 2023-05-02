package com.example.myruns4


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {
    val profileImage = MutableLiveData<Bitmap>()


}