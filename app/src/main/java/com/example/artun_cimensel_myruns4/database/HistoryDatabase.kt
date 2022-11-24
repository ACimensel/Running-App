package com.example.artun_cimensel_myruns4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HistoryEntry::class], version = 1)
@TypeConverters(Converters::class)
abstract class HistoryDatabase : RoomDatabase() { //XD: Room automatically generates implementations of your abstract CommentDatabase class.
    abstract val historyDatabaseDao: HistoryDatabaseDao

    companion object{
        //The Volatile keyword guarantees visibility of changes to the INSTANCE variable across threads
        @Volatile
        private var INSTANCE: HistoryDatabase? = null

        fun getInstance(context: Context) : HistoryDatabase{
            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(context.applicationContext,
                        HistoryDatabase::class.java, "history_table").build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}