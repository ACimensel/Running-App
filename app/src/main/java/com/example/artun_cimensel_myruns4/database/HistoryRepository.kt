package com.example.artun_cimensel_myruns4.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

//A Repository manages queries and allows you to use multiple backends.
// In the most common example, the Repository implements the logic for
// deciding whether to fetch data from a network or use results cached in a local database.
class HistoryRepository(private val commentDatabaseDao: HistoryDatabaseDao) {

    val allHistoryEntries: Flow<List<HistoryEntry>> = commentDatabaseDao.getAllHistoryEntries()

    fun insert(historyEntry: HistoryEntry){
        CoroutineScope(Dispatchers.IO).launch{
            commentDatabaseDao.insertHistoryEntry(historyEntry)
        }
    }

    fun delete(id: Long){
        CoroutineScope(Dispatchers.IO).launch {
            commentDatabaseDao.deleteHistoryEntry(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(Dispatchers.IO).launch {
            commentDatabaseDao.deleteAllHistoryEntries()
        }
    }
}