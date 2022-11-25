package com.example.artun_cimensel_myruns4.activities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.classifier.Globals
import com.example.artun_cimensel_myruns4.classifier.SensorService


class AutoActivity : AppCompatActivity() {
    private lateinit var xLabel: TextView
    private lateinit var yLabel: TextView
    private lateinit var zLabel: TextView
    private lateinit var titleLabel: TextView
    private lateinit var sensorManager: SensorManager
    private lateinit var mServiceIntent: Intent
    private lateinit var mClassifierIntentFilter: IntentFilter
    private val classifierBroadcastReceiver = ClassifierBroadcastReceiver(Handler(Looper.getMainLooper()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto)

        xLabel = findViewById(R.id.xval)
        yLabel = findViewById(R.id.yval)
        zLabel = findViewById(R.id.zval)
        titleLabel = findViewById(R.id.textView)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager


        mClassifierIntentFilter = IntentFilter()
        mClassifierIntentFilter.addAction(Globals.ACTION_MOTION_UPDATED)

        // New code here
        mServiceIntent = Intent(this, SensorService::class.java)
        startService(mServiceIntent)
    }

    override fun onResume() {
        registerReceiver(classifierBroadcastReceiver, mClassifierIntentFilter)
        super.onResume()
    }

    override fun onPause() {
        unregisterReceiver(classifierBroadcastReceiver)
        super.onPause()
    }

    override fun onBackPressed() {
        stopService(mServiceIntent)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancel(Globals.NOTIFICATION_ID)

        super.onBackPressed()
    }

    override fun onDestroy() {
        stopService(mServiceIntent)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).cancelAll()

        finish()
        super.onDestroy()
    }

    class ClassifierBroadcastReceiver(handler: Handler) : BroadcastReceiver() {
        private val handler : Handler // Handler used to execute code on the UI thread

        init {
            this.handler = handler
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            val classifier = intent?.getDoubleExtra(Globals.KEY_CLASS_RESULT, -1.0)


            // Post the UI updating code to our Handler
            handler.post {
//            Log.d("DEBUG:", "____________________onReceive $classifier")
                Toast.makeText(context, classifier.toString(), Toast.LENGTH_SHORT).show() // TODO remove

//                if(classifier == 0.0)
//                    titleLabel.text = "You are idle."
            }
        }
    }
}