package com.example.artun_cimensel_myruns4.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.artun_cimensel_myruns4.activities.ManualEntriesActivity
import com.example.artun_cimensel_myruns4.activities.MapActivity
import com.example.artun_cimensel_myruns4.R

class StartFragment : Fragment() {
    companion object{
        val inputTypes = arrayOf("Manual Entry", "GPS", "Automatic") //Input types
        val activityTypes = arrayOf("Running", "Walking", "Standing", "Cycling", "Hiking",
            "Downhill Skiing", "Cross-Country Skiing", "Snowboarding", "Skating",
            "Swimming", "Mountain Biking", "Wheelchair", "Elliptical", "Other") //Activity types
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputsSpinner = view.findViewById(R.id.spinner_input_type) as Spinner
        val activitiesSpinner = view.findViewById(R.id.spinner_activity_type) as Spinner

        val inputsAdapter = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, inputTypes)
        val activitiesAdaptor = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, activityTypes)

        inputsSpinner.adapter = inputsAdapter
        activitiesSpinner.adapter = activitiesAdaptor

        val button = view.findViewById(R.id.button_start) as Button
        button.setOnClickListener{
            if(inputsSpinner.selectedItemPosition == 0){
                val intent = Intent(requireActivity(), ManualEntriesActivity::class.java)
                intent.putExtra(ManualEntriesActivity.KEY_ACTIVITY, activitiesSpinner.selectedItemPosition)

                startActivity(intent)
            }
            else if(inputsSpinner.selectedItemPosition == 1){
                val intent = Intent(requireActivity(), MapActivity::class.java)
                intent.putExtra(MapActivity.KEY_ENTRY, false)
                intent.putExtra(MapActivity.KEY_INPUT, inputsSpinner.selectedItemPosition)
                intent.putExtra(MapActivity.KEY_ACTIVITY, activitiesSpinner.selectedItemPosition)

                startActivity(intent)
            }
        }
    }
}