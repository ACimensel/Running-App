package com.example.artun_cimensel_myruns4.classifier

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.activities.AutoActivity
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instance
import weka.core.Instances
import weka.core.converters.ArffSaver
import weka.core.converters.ConverterUtils
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


class SensorService : Service(), SensorEventListener {
    private val mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 1 // reduced by 1
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mAsyncTask: OnSensorChangedTask
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    val mdf = DecimalFormat("#.##")

    override fun onCreate() {
        super.onCreate()
        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)

//        // Create the container for attributes
//        val allAttr = ArrayList<Attribute>()
//
//        // Adding FFT coefficient attributes
//        val df = DecimalFormat("0000")
//        for (i in 0 until Globals.ACCELEROMETER_BLOCK_CAPACITY) {
//            allAttr.add(Attribute(Globals.FEAT_FFT_COEF_LABEL + df.format(i.toLong())))
//        }
//        // Adding the max feature
//        allAttr.add(Attribute(Globals.FEAT_MAX_LABEL))
//
//        val i = Intent(this, AutoActivity::class.java)
//        // Read:
//        // http://developer.android.com/guide/topics/manifest/activity-element.html#lmode
//        // IMPORTANT!. no re-create activity
//        i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//        val pi = PendingIntent.getActivity(this, 0, i, 0)
//        val notification: Notification = Notification.Builder(this)
//                .setContentTitle(
//                        applicationContext.getString(
//                                R.string.ui_sensor_service_notification_title))
//                .setContentText(
//                        resources
//                                .getString(
//                                        R.string.ui_sensor_service_notification_content))
//                .setSmallIcon(R.drawable.drink).setContentIntent(pi).build()
//        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notification.flags = (notification.flags
//                or Notification.FLAG_ONGOING_EVENT)
//        notificationManager.notify(0, notification)
        mAsyncTask = OnSensorChangedTask()
        mAsyncTask.execute()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        mAsyncTask.cancel(true)
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mSensorManager.unregisterListener(this)
        Log.i("", "")
        super.onDestroy()
    }

    inner class OnSensorChangedTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg arg0: Void?): Void? {
            val inst: Instance = DenseInstance(mFeatLen)
            var blockSize = 0
            val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            var max = Double.MIN_VALUE
            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled() == true) {
                        return null
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().toDouble()
                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0

                        // time = System.currentTimeMillis();
                        max = .0
                        for (`val` in accBlock) {
                            if (max < `val`) {
                                max = `val`
                            }
                        }
                        fft.fft(accBlock, im)
                        for (i in accBlock.indices) {
                            val mag = Math.sqrt(accBlock[i] * accBlock[i] + im[i]
                                    * im[i])
                            inst.setValue(i, mag)
                            im[i] = .0 // Clear the field
                        }

                        // Append max after frequency component
                        inst.setValue(Globals.ACCELEROMETER_BLOCK_CAPACITY, max)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val m = Math.sqrt((event.values[0] * event.values[0] + event.values[1] * event.values[1]
                    + (event.values[2] * event.values[2])).toDouble())

//            // Inserts the specified element into this queue if it is possible
//            // to do so immediately without violating capacity restrictions,
//            // returning true upon success and throwing an IllegalStateException
//            // if no space is currently available. When using a
//            // capacity-restricted queue, it is generally preferable to use
//            // offer.
//            try {
//                mAccBuffer.add(m)
//            } catch (e: IllegalStateException) {
//
//                // Exception happens when reach the capacity.
//                // Doubling the buffer. ListBlockingQueue has no such issue,
//                // But generally has worse performance
//                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
//                mAccBuffer.drainTo(newBuf)
//                mAccBuffer = newBuf
//                mAccBuffer.add(m)
//            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}