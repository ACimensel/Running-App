package com.example.artun_cimensel_myruns4.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.lang.IllegalArgumentException

class HistoryViewModel(private val repository: HistoryRepository) : ViewModel() {
    val allHistoryEntriesLiveData: LiveData<List<HistoryEntry>> = repository.allHistoryEntries.asLiveData()

    fun insert(historyEntry: HistoryEntry) {
        repository.insert(historyEntry)
    }

    fun deleteFirst(){
        val historyEntriesList = allHistoryEntriesLiveData.value
        if (historyEntriesList != null && historyEntriesList.isNotEmpty()){
            val id = historyEntriesList[0].id
            repository.delete(id)
        }
    }

    fun deletePosition(pos: Int){
        val historyEntriesList = allHistoryEntriesLiveData.value
        if (historyEntriesList != null && historyEntriesList.isNotEmpty()){
            val id = historyEntriesList[pos].id
            repository.delete(id)
        }
    }

    fun deleteAll(){
        val historyEntriesList = allHistoryEntriesLiveData.value
        if (historyEntriesList != null && historyEntriesList.isNotEmpty())
            repository.deleteAll()
    }
}

class HistoryViewModelFactory (private val repository: HistoryRepository) : ViewModelProvider.Factory {
    override fun<T: ViewModel> create(modelClass: Class<T>) : T{ //create() creates a new instance of the modelClass, which is CommentViewModel in this case.
        if(modelClass.isAssignableFrom(HistoryViewModel::class.java))
            return HistoryViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}