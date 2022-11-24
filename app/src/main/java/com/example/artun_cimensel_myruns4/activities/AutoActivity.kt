package com.example.artun_cimensel_myruns4.activities

import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.artun_cimensel_myruns4.R

class AutoActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var xLabel: TextView
    private lateinit var yLabel: TextView
    private lateinit var zLabel: TextView
    private lateinit var titleLabel: TextView
    private lateinit var sensorManager: SensorManager
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
    }

    override fun onResume() {
        super.onResume()
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            x = (event.values[0] / SensorManager.GRAVITY_EARTH).toDouble()
            y = (event.values[1] / SensorManager.GRAVITY_EARTH).toDouble()
            z = (event.values[2] / SensorManager.GRAVITY_EARTH).toDouble()
            xLabel.text = "X axis: $x"
            yLabel.text = "Y axis: $y"
            zLabel.text = "Z axis: $z"
            checkShake()
        }
    }

    private fun checkShake() {
        val magnitude = Math.sqrt(x * x + y * y + z * z)
        currentTime = System.currentTimeMillis()
        if (magnitude > 3 && currentTime - lastTime > 300) {
            titleLabel.setBackgroundColor(Color.RED)
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel: 123456")
            startActivity(intent)
            lastTime = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}