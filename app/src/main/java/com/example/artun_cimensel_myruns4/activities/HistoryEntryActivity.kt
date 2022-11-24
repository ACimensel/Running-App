package com.example.artun_cimensel_myruns4.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.artun_cimensel_myruns4.R
import kotlin.properties.Delegates


class HistoryEntryActivity : AppCompatActivity() {
    private var mIndex by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_entry)

        mIndex = intent.getIntExtra("list_index", -1)

        val textInput = findViewById<EditText>(R.id.editText_input)
        val textActivity = findViewById<EditText>(R.id.editText_activity)
        val textDateTime = findViewById<EditText>(R.id.editText_dateTime)
        val textDuration = findViewById<EditText>(R.id.editText_duration)
        val textDistance = findViewById<EditText>(R.id.editText_distance)
        val textCalories = findViewById<EditText>(R.id.editText_calories)
        val textHeartRate = findViewById<EditText>(R.id.editText_heartRate)

        textInput.setText(intent.getStringExtra("input_type")!!)
        textActivity.setText(intent.getStringExtra("activity_type")!!)
        textDateTime.setText(intent.getStringExtra("date_time")!!)
        textDuration.setText(intent.getStringExtra("duration")!!)
        textDistance.setText(intent.getStringExtra("distance")!!)
        textCalories.setText(intent.getStringExtra("calories")!!)
        textHeartRate.setText(intent.getStringExtra("heart_rate")!!)
    }

    fun deleteEntry(view: View) {
        val returnIntent = Intent()
        returnIntent.putExtra("return_index", mIndex)
        setResult(RESULT_OK, returnIntent)
        finish()
    }
}
