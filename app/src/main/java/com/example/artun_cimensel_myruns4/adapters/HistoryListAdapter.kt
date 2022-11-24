package com.example.artun_cimensel_myruns4.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.Util
import com.example.artun_cimensel_myruns4.database.HistoryEntry
import com.example.artun_cimensel_myruns4.fragments.StartFragment

class HistoryListAdapter(private val context: Context, private var historyList: List<HistoryEntry>) : BaseAdapter(){
    private var isMetric = true

    override fun getItem(position: Int): Any {
        return historyList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return this.historyList.size
    }

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.history_list_item,null)

        val distanceStr =
            if(isMetric) Util.distanceToCorrectUnitStr(historyList[pos].distance, true)
            else Util.distanceToCorrectUnitStr(historyList[pos].distance, false)

        val inputStr = StartFragment.inputTypes[historyList[pos].inputType]
        val activityStr = StartFragment.activityTypes[historyList[pos].activityType]
        val durationStr = Util.durationToMinSecStr(historyList[pos].duration)

        val textTopRow = view.findViewById(R.id.text_topRow) as TextView
        val textBottomRow = view.findViewById(R.id.text_bottomRow) as TextView

        textTopRow.text = "$inputStr: $activityStr, ${Util.calendarToStr(historyList[pos].dateTime)}"
        textBottomRow.text = "$distanceStr, $durationStr"

        return view
    }

    fun replace(newHistoryList: List<HistoryEntry>){
        historyList = newHistoryList
    }

    fun setDistanceUnits(_isMetric: Boolean){
        isMetric = _isMetric
    }
}