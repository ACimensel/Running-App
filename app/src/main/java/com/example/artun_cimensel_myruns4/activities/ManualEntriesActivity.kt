package com.example.artun_cimensel_myruns4.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.Util
import com.example.artun_cimensel_myruns4.database.*
import com.example.artun_cimensel_myruns4.fragments.ManualEntriesDialog
import java.util.*

class ManualEntriesActivity : AppCompatActivity() {
    companion object{
        const val KEY_ACTIVITY = "activity_type"
    }

    private val informationTypes = arrayOf("Date", "Time", "Duration",
        "Distance", "Calories", "Heart Rate", "Comment") //Information types

    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: HistoryDatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyEntry: HistoryEntry

    private lateinit var sPrefs: SharedPreferences
    private lateinit var calendar: Calendar
    private var isDateSet = false
    private var year = 0
    private var month = 0
    private var day = 0
    private var isTimeSet = false
    private var hour = 0
    private var minute = 0
    private var second = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_entries)

        sPrefs = this.getSharedPreferences("SettingsDialogPrefs", AppCompatActivity.MODE_PRIVATE)

        database = HistoryDatabase.getInstance(this)
        databaseDao = database.historyDatabaseDao
        repository = HistoryRepository(databaseDao)
        viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory).get(HistoryViewModel::class.java)
        historyEntry = HistoryEntry()

        historyEntry.inputType = 0
        historyEntry.activityType = intent.extras!!.getInt(KEY_ACTIVITY)

        val infoTypesList = findViewById<ListView>(R.id.listView_exerciseInfo)
        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, informationTypes)
        infoTypesList.adapter = listAdapter

        infoTypesList.setOnItemClickListener { _, _, position, _ ->
            showCustomDialog(position)
        }
    }

    private fun showCustomDialog(position: Int) {
        val dialog = ManualEntriesDialog()
        val bundle = Bundle()

        bundle.putInt(ManualEntriesDialog.KEY_IDX, position)
        bundle.putString(ManualEntriesDialog.KEY_TITLE, informationTypes[position])

        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "ExerciseInfoDialog")
    }

    fun onButtonClick(view: View) {
        if(view.id == R.id.btn_save){
            calendar = Calendar.getInstance()

            if(isDateSet){
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
            }

            if(isTimeSet){
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                calendar.set(Calendar.SECOND, second)
            }

            historyEntry.dateTime = calendar
            historyViewModel.insert(historyEntry)
        }
        else if(view.id == R.id.btn_cancel){
            Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    fun setOther(index: Int, other: String) {
        val isImperial = sPrefs.getInt("UnitBtnId", R.id.metric) == R.id.imperial

        if (other.isNotEmpty()){
            when (index) {
                2 -> historyEntry.duration = other.toDouble()
                3 -> {
                    var distance = other.toDouble()
                    if(isImperial) distance = Util.milesToKm(distance)
                    historyEntry.distance = distance
                }
                4 -> historyEntry.calorie = other.toDouble()
                5 -> historyEntry.heartRate = other.toDouble()
                6 -> historyEntry.comment = other
            }
        }
    }

    fun setTime(_hour: Int, _minute: Int, _second: Int = 0) {
        hour = _hour
        minute = _minute
        second = _second
        isTimeSet = true
    }

    fun setDate(_year: Int, _month: Int, _day: Int) {
        year = _year
        month = _month
        day = _day
        isDateSet = true
    }
}