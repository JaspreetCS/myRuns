package com.example.myruns4

import androidx.lifecycle.LiveData
import androidx.room.Dao

import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface ManualDatabaseDao {

    @Insert
    suspend fun insertManual(manual: Manual)

    //A Flow is an async sequence of values
    //Flow produces values one at a time (instead of all at once) that can generate values
    //from async operations like network requests, database calls, or other async code.
    //It supports coroutines throughout its API, so you can transform a flow using coroutines as well!
    //Code inside the flow { ... } builder block can suspend. So the function is no longer marked with suspend modifier.

    @Query("SELECT * FROM manual_table")
    fun getAllManuals(): Flow<List<Manual>>

    @Query("DELETE FROM manual_table")
    suspend fun deleteAll()

    @Query("DELETE FROM manual_table WHERE id = :key")
    suspend fun deleteManual(key: Long)

}