package com.example.myruns4

import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class ManualRepository(private val manualDatabaseDao: ManualDatabaseDao) {

    val allManuals: Flow<List<Manual>> = manualDatabaseDao.getAllManuals()

    fun insert(manual: Manual){
        CoroutineScope(IO).launch{
            manualDatabaseDao.insertManual(manual)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch {
            manualDatabaseDao.deleteManual(id)
        }
    }


}