package com.example.artun_cimensel_myruns4.activities

import android.app.NotificationManager
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.classifier.Globals
import com.example.artun_cimensel_myruns4.classifier.SensorService

class AutoActivity : AppCompatActivity()
//    , SensorEventListener
{
    private lateinit var xLabel: TextView
    private lateinit var yLabel: TextView
    private lateinit var zLabel: TextView
    private lateinit var titleLabel: TextView
    private lateinit var sensorManager: SensorManager
    private lateinit var mServiceIntent: Intent

    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0
    private var lastTime: Long = 0
    private var currentTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto)

        xLabel = findViewById(R.id.xval)
        yLabel = findViewById(R.id.yval)
        zLabel = findViewById(R.id.zval)
        titleLabel = findViewById(R.id.textView)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        lastTime = System.currentTimeMillis()

        // New code here
        mServiceIntent = Intent(this, SensorService::class.java)
        startService(mServiceIntent)
    }

//    override fun onResume() {
//        super.onResume()
//        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        sensorManager.unregisterListener(this)
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
//            x = (event.values[0] / SensorManager.GRAVITY_EARTH).toDouble()
//            y = (event.values[1] / SensorManager.GRAVITY_EARTH).toDouble()
//            z = (event.values[2] / SensorManager.GRAVITY_EARTH).toDouble()
//            xLabel.text = "X axis: $x"
//            yLabel.text = "Y axis: $y"
//            zLabel.text = "Z axis: $z"
//
////            checkShake()
//            val magnitude = Math.sqrt(x * x + y * y + z * z)
//            titleLabel.text = magnitude.toString()
//            // collect 64 data points of magnitude, find max
//            // convert window of data to fft. fft data is mirrored, only need first 32 bins, can use all 64
//            // add max to end of fft array (size 65 now)
//        }
//    }

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

//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}