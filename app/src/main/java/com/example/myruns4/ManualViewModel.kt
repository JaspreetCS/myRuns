package com.example.myruns4

import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


class ManualViewModel(private val repository: ManualRepository) : ViewModel() {
    val allManualsLiveData: LiveData<List<Manual>> = repository.allManuals.asLiveData()

    val duration = MutableLiveData<Double>()
    val distance = MutableLiveData<Double>()
    val heartRate = MutableLiveData<Int>()
    val calories = MutableLiveData<Double>()
    val comment = MutableLiveData<String>()
    val locations = MutableLiveData<ArrayList<LatLng>>()


    init {
        duration.value = 0.0
        distance.value = 0.0
        heartRate.value = 0
        calories.value = 0.0
        comment.value = ""
        locations.value = ArrayList()

    }

    fun insert(manual: Manual) {
        repository.insert(manual)
    }

    fun delete(id: Long){
        val manualList = allManualsLiveData.value
        if (manualList != null && manualList.size > 0){
            repository.delete(id)
        }
    }
}

class ManualViewModelFactory (private val repository: ManualRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{
        if(modelClass.isAssignableFrom(ManualViewModel::class.java))
            return ManualViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


