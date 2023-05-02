package com.example.myruns4

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Manual::class], version = 1)
abstract class ManualDatabase : RoomDatabase() { //Room automatically generates implementations of abstract ManualDatabase class.
    abstract val manualDatabaseDao: ManualDatabaseDao

    companion object{
        //The Volatile keyword guarantees visibility of changes to the INSTANCE variable across threads
        @Volatile
        private var INSTANCE: ManualDatabase? = null

        fun getInstance(context: Context) : ManualDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        ManualDatabase::class.java, "manual_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}