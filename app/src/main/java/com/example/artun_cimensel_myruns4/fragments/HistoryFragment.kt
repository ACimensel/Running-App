package com.example.artun_cimensel_myruns4.fragments

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.Util
import com.example.artun_cimensel_myruns4.activities.HistoryEntryActivity
import com.example.artun_cimensel_myruns4.activities.MapActivity
import com.example.artun_cimensel_myruns4.adapters.HistoryListAdapter
import com.example.artun_cimensel_myruns4.database.*
import java.util.*

class HistoryFragment : Fragment() {
    private lateinit var addButton: Button
    private lateinit var deleteButton: Button
    private lateinit var deleteAllButton: Button
    private lateinit var myListView: ListView

    private lateinit var arrayList: ArrayList<HistoryEntry>
    private lateinit var arrayAdapter: HistoryListAdapter

    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: HistoryDatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var result: ActivityResultLauncher<Intent>
    private lateinit var sPrefs: SharedPreferences

    override fun onResume() {
        super.onResume()

        when (sPrefs.getInt("UnitBtnId", R.id.metric)) {
            R.id.imperial -> arrayAdapter.setDistanceUnits(false)
            else -> arrayAdapter.setDistanceUnits(true)
        }
        arrayAdapter.notifyDataSetChanged()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sPrefs = requireActivity().getSharedPreferences("SettingsDialogPrefs", AppCompatActivity.MODE_PRIVATE)

        myListView = view.findViewById(R.id.list)
        addButton = view.findViewById(R.id.add)
        deleteButton = view.findViewById(R.id.delete)
        deleteAllButton = view.findViewById(R.id.deleteall)

        arrayList = ArrayList()
        arrayAdapter = HistoryListAdapter(requireActivity(), arrayList)
        myListView.adapter = arrayAdapter

        database = HistoryDatabase.getInstance(requireActivity())
        databaseDao = database.historyDatabaseDao
        repository = HistoryRepository(databaseDao)
        viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(HistoryViewModel::class.java)

        historyViewModel.allHistoryEntriesLiveData.observe(requireActivity()) {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        }

        myListView.setOnItemClickListener { parent, _, pos, _ ->
            val entry = parent.getItemAtPosition(pos) as HistoryEntry
            val intent: Intent

            when (entry.inputType) {
                0 -> {
                    intent = Intent(requireActivity(), HistoryEntryActivity::class.java)
                    intent.putExtra("list_index", pos)
                    intent.putExtra("input_type", StartFragment.inputTypes[entry.inputType])
                    intent.putExtra("activity_type", StartFragment.activityTypes[entry.activityType])
                    intent.putExtra("date_time", Util.calendarToStr(entry.dateTime))
                    intent.putExtra("duration", Util.durationToMinSecStr(entry.duration))
                    intent.putExtra("calories", entry.calorie.toInt().toString() + " cals")
                    intent.putExtra("heart_rate", entry.heartRate.toInt().toString() + " bpm")
                }
                1 -> {
                    intent = Intent(requireActivity(), MapActivity::class.java)
                    intent.putExtra("list_index", pos)
                    intent.putExtra(MapActivity.KEY_ENTRY, true)
                    intent.putExtra(MapActivity.KEY_ACTIVITY, entry.activityType)
                    intent.putExtra(MapActivity.KEY_CALORIES, entry.calorie)
                    intent.putExtra(MapActivity.KEY_LIST, entry.locationList)
                }
                else -> intent = Intent(requireActivity(), HistoryEntryActivity::class.java) // TODO Automatic for MyRuns5
            }

            when (sPrefs.getInt("UnitBtnId", R.id.metric)) {
                R.id.imperial -> {
                    intent.putExtra(MapActivity.KEY_DISTANCE, Util.distanceToCorrectUnitStr(entry.distance, false))
                    intent.putExtra(MapActivity.KEY_CLIMB, Util.distanceToCorrectUnitStr(entry.climb, false))
                    intent.putExtra(MapActivity.KEY_AVG_SPEED, Util.speedToCorrectUnitStr(entry.avgSpeed, false))
                }
                else -> {
                    intent.putExtra(MapActivity.KEY_DISTANCE, Util.distanceToCorrectUnitStr(entry.distance, true))
                    intent.putExtra(MapActivity.KEY_CLIMB, Util.distanceToCorrectUnitStr(entry.climb, true))
                    intent.putExtra(MapActivity.KEY_AVG_SPEED, Util.speedToCorrectUnitStr(entry.avgSpeed, true))
                }
            }

            result.launch(intent)
        }

        result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK ) {
                val index = result.data?.getIntExtra("return_index", -1) as Int
                if (index > -1) {
                    historyViewModel.deletePosition(index)
                }
            }
        }

//        addButton.setOnClickListener {
//            val historyEntry = HistoryEntry()
//
//            historyEntry.inputType = 0
//            historyEntry.activityType = 0
//            historyEntry.distance = 10.2
//            historyEntry.duration = 0.0
//            historyEntry.dateTime = Calendar.getInstance()
//
//            historyViewModel.insert(historyEntry)
//        }
//
//        deleteButton.setOnClickListener {
//            historyViewModel.deleteFirst()
//        }
//
//        deleteAllButton.setOnClickListener {
//            historyViewModel.deleteAll()
//        }
    }
}