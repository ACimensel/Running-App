package com.example.artun_cimensel_myruns4.classifier

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import weka.core.Attribute
import weka.core.DenseInstance
import weka.core.Instance
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.ArrayBlockingQueue


class SensorService : Service(), SensorEventListener {
    private val mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 1 // reduced by 1
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mAsyncTask: OnSensorChangedTask
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    private val classificationBroadCastIntent = Intent()

    override fun onCreate() {
        super.onCreate()
        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)
        classificationBroadCastIntent.action = Globals.ACTION_MOTION_UPDATED
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)

        // Create the container for attributes
        val allAttr = ArrayList<Attribute>()

        // Adding FFT coefficient attributes
        val df = DecimalFormat("0000")
        for (i in 0 until Globals.ACCELEROMETER_BLOCK_CAPACITY) {
            allAttr.add(Attribute(Globals.FEAT_FFT_COEF_LABEL + df.format(i.toLong())))
        }
        // Adding the max feature
        allAttr.add(Attribute(Globals.FEAT_MAX_LABEL))

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

                        //Classify
                        val classVal = WekaClassifier.classify(inst.toDoubleArray())

                        classificationBroadCastIntent.putExtra(Globals.KEY_CLASS_RESULT, classVal)
                        this@SensorService.sendBroadcast(classificationBroadCastIntent)
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

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.
            try {
                mAccBuffer.add(m)
            } catch (e: IllegalStateException) {
                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(m)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    internal object WekaClassifier {
        @Throws(java.lang.Exception::class)
        fun classify(i: DoubleArray): Double {
            var p = Double.NaN
            p = N3be4795e0(i)
            return p
        }

        fun N3be4795e0(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 0.0
            } else if ((i[0] as Double?)!!.toDouble() <= 136.188134) {
                p = N7d881a661(i)
            } else if ((i[0] as Double?)!!.toDouble() > 136.188134) {
                p = N78e7076e9(i)
            }
            return p
        }

        fun N7d881a661(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[10] == null) {
                p = 0.0
            } else if ((i[10] as Double?)!!.toDouble() <= 0.832137) {
                p = 0.0
            } else if ((i[10] as Double?)!!.toDouble() > 0.832137) {
                p = Nbb44ea72(i)
            }
            return p
        }

        fun Nbb44ea72(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[32] == null) {
                p = 1.0
            } else if ((i[32] as Double?)!!.toDouble() <= 0.237618) {
                p = 1.0
            } else if ((i[32] as Double?)!!.toDouble() > 0.237618) {
                p = N240f5bfa3(i)
            }
            return p
        }

        fun N240f5bfa3(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 0.0
            } else if ((i[0] as Double?)!!.toDouble() <= 53.452704) {
                p = 0.0
            } else if ((i[0] as Double?)!!.toDouble() > 53.452704) {
                p = N3c39e8ac4(i)
            }
            return p
        }

        fun N3c39e8ac4(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[12] == null) {
                p = 1.0
            } else if ((i[12] as Double?)!!.toDouble() <= 0.710789) {
                p = N15f8e675(i)
            } else if ((i[12] as Double?)!!.toDouble() > 0.710789) {
                p = N28b7299d6(i)
            }
            return p
        }

        fun N15f8e675(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[15] == null) {
                p = 0.0
            } else if ((i[15] as Double?)!!.toDouble() <= 0.432598) {
                p = 0.0
            } else if ((i[15] as Double?)!!.toDouble() > 0.432598) {
                p = 1.0
            }
            return p
        }

        fun N28b7299d6(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[7] == null) {
                p = 1.0
            } else if ((i[7] as Double?)!!.toDouble() <= 0.615272) {
                p = 1.0
            } else if ((i[7] as Double?)!!.toDouble() > 0.615272) {
                p = N76b967fa7(i)
            }
            return p
        }

        fun N76b967fa7(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[29] == null) {
                p = 0.0
            } else if ((i[29] as Double?)!!.toDouble() <= 0.559047) {
                p = N5b69d098(i)
            } else if ((i[29] as Double?)!!.toDouble() > 0.559047) {
                p = 0.0
            }
            return p
        }

        fun N5b69d098(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[10] == null) {
                p = 0.0
            } else if ((i[10] as Double?)!!.toDouble() <= 1.308712) {
                p = 0.0
            } else if ((i[10] as Double?)!!.toDouble() > 1.308712) {
                p = 1.0
            }
            return p
        }

        fun N78e7076e9(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 1.0
            } else if ((i[0] as Double?)!!.toDouble() <= 693.555633) {
                p = N690d322e10(i)
            } else if ((i[0] as Double?)!!.toDouble() > 693.555633) {
                p = N1e8b10ae53(i)
            }
            return p
        }

        fun N690d322e10(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 1.0
            } else if ((i[0] as Double?)!!.toDouble() <= 582.859601) {
                p = N54360e4211(i)
            } else if ((i[0] as Double?)!!.toDouble() > 582.859601) {
                p = N4fdc914445(i)
            }
            return p
        }

        fun N54360e4211(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 1.0
            } else if ((i[1] as Double?)!!.toDouble() <= 148.603609) {
                p = N65d99f1812(i)
            } else if ((i[1] as Double?)!!.toDouble() > 148.603609) {
                p = N4836d51643(i)
            }
            return p
        }

        fun N65d99f1812(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 1.0
            } else if ((i[0] as Double?)!!.toDouble() <= 204.422235) {
                p = N254538ba13(i)
            } else if ((i[0] as Double?)!!.toDouble() > 204.422235) {
                p = N24c137819(i)
            }
            return p
        }

        fun N254538ba13(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[14] == null) {
                p = 1.0
            } else if ((i[14] as Double?)!!.toDouble() <= 3.753324) {
                p = N25d9e27e14(i)
            } else if ((i[14] as Double?)!!.toDouble() > 3.753324) {
                p = 0.0
            }
            return p
        }

        fun N25d9e27e14(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 1.0
            } else if ((i[1] as Double?)!!.toDouble() <= 36.208025) {
                p = N41f9bc9915(i)
            } else if ((i[1] as Double?)!!.toDouble() > 36.208025) {
                p = 1.0
            }
            return p
        }

        fun N41f9bc9915(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[4] == null) {
                p = 1.0
            } else if ((i[4] as Double?)!!.toDouble() <= 8.685273) {
                p = 1.0
            } else if ((i[4] as Double?)!!.toDouble() > 8.685273) {
                p = N6871cae116(i)
            }
            return p
        }

        fun N6871cae116(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[5] == null) {
                p = 1.0
            } else if ((i[5] as Double?)!!.toDouble() <= 12.418427) {
                p = N64fdbfbd17(i)
            } else if ((i[5] as Double?)!!.toDouble() > 12.418427) {
                p = 2.0
            }
            return p
        }

        fun N64fdbfbd17(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[12] == null) {
                p = 1.0
            } else if ((i[12] as Double?)!!.toDouble() <= 2.291842) {
                p = Nedd7b4e18(i)
            } else if ((i[12] as Double?)!!.toDouble() > 2.291842) {
                p = 1.0
            }
            return p
        }

        fun Nedd7b4e18(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[9] == null) {
                p = 1.0
            } else if ((i[9] as Double?)!!.toDouble() <= 2.332618) {
                p = 1.0
            } else if ((i[9] as Double?)!!.toDouble() > 2.332618) {
                p = 2.0
            }
            return p
        }

        fun N24c137819(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[6] == null) {
                p = 1.0
            } else if ((i[6] as Double?)!!.toDouble() <= 10.147813) {
                p = N640ce71220(i)
            } else if ((i[6] as Double?)!!.toDouble() > 10.147813) {
                p = N13be1afc29(i)
            }
            return p
        }

        fun N640ce71220(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[3] == null) {
                p = 1.0
            } else if ((i[3] as Double?)!!.toDouble() <= 16.119768) {
                p = 1.0
            } else if ((i[3] as Double?)!!.toDouble() > 16.119768) {
                p = N6d05ac7921(i)
            }
            return p
        }

        fun N6d05ac7921(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 1.0
            } else if ((i[1] as Double?)!!.toDouble() <= 32.096873) {
                p = N8e7bd9c22(i)
            } else if ((i[1] as Double?)!!.toDouble() > 32.096873) {
                p = N63914a8827(i)
            }
            return p
        }

        fun N8e7bd9c22(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[64] == null) {
                p = 1.0
            } else if ((i[64] as Double?)!!.toDouble() <= 8.292537) {
                p = N6ad2b82b23(i)
            } else if ((i[64] as Double?)!!.toDouble() > 8.292537) {
                p = 1.0
            }
            return p
        }

        fun N6ad2b82b23(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[4] == null) {
                p = 1.0
            } else if ((i[4] as Double?)!!.toDouble() <= 16.119039) {
                p = N6965e61424(i)
            } else if ((i[4] as Double?)!!.toDouble() > 16.119039) {
                p = 2.0
            }
            return p
        }

        fun N6965e61424(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[3] == null) {
                p = 2.0
            } else if ((i[3] as Double?)!!.toDouble() <= 18.433211) {
                p = 2.0
            } else if ((i[3] as Double?)!!.toDouble() > 18.433211) {
                p = N29c3e92525(i)
            }
            return p
        }

        fun N29c3e92525(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[7] == null) {
                p = 1.0
            } else if ((i[7] as Double?)!!.toDouble() <= 5.349538) {
                p = 1.0
            } else if ((i[7] as Double?)!!.toDouble() > 5.349538) {
                p = N1cfdaadc26(i)
            }
            return p
        }

        fun N1cfdaadc26(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 0.0
            } else if ((i[1] as Double?)!!.toDouble() <= 23.707421) {
                p = 0.0
            } else if ((i[1] as Double?)!!.toDouble() > 23.707421) {
                p = 2.0
            }
            return p
        }

        fun N63914a8827(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[9] == null) {
                p = 1.0
            } else if ((i[9] as Double?)!!.toDouble() <= 12.258995) {
                p = 1.0
            } else if ((i[9] as Double?)!!.toDouble() > 12.258995) {
                p = N258245a828(i)
            }
            return p
        }

        fun N258245a828(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[8] == null) {
                p = 2.0
            } else if ((i[8] as Double?)!!.toDouble() <= 9.834975) {
                p = 2.0
            } else if ((i[8] as Double?)!!.toDouble() > 9.834975) {
                p = 1.0
            }
            return p
        }

        fun N13be1afc29(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[4] == null) {
                p = 1.0
            } else if ((i[4] as Double?)!!.toDouble() <= 12.315205) {
                p = N6264f91d30(i)
            } else if ((i[4] as Double?)!!.toDouble() > 12.315205) {
                p = N1ab3a37c34(i)
            }
            return p
        }

        fun N6264f91d30(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 2.0
            } else if ((i[1] as Double?)!!.toDouble() <= 30.392303) {
                p = Nbc83ad331(i)
            } else if ((i[1] as Double?)!!.toDouble() > 30.392303) {
                p = N3cd0910732(i)
            }
            return p
        }

        fun Nbc83ad331(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[4] == null) {
                p = 2.0
            } else if ((i[4] as Double?)!!.toDouble() <= 9.881746) {
                p = 2.0
            } else if ((i[4] as Double?)!!.toDouble() > 9.881746) {
                p = 1.0
            }
            return p
        }

        fun N3cd0910732(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[2] == null) {
                p = 1.0
            } else if ((i[2] as Double?)!!.toDouble() <= 121.727021) {
                p = N5f5a159b33(i)
            } else if ((i[2] as Double?)!!.toDouble() > 121.727021) {
                p = 2.0
            }
            return p
        }

        fun N5f5a159b33(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[13] == null) {
                p = 2.0
            } else if ((i[13] as Double?)!!.toDouble() <= 1.631933) {
                p = 2.0
            } else if ((i[13] as Double?)!!.toDouble() > 1.631933) {
                p = 1.0
            }
            return p
        }

        fun N1ab3a37c34(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 1.0
            } else if ((i[0] as Double?)!!.toDouble() <= 542.079476) {
                p = N50e71c5b35(i)
            } else if ((i[0] as Double?)!!.toDouble() > 542.079476) {
                p = N67fed1af37(i)
            }
            return p
        }

        fun N50e71c5b35(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 1.0
            } else if ((i[0] as Double?)!!.toDouble() <= 249.826761) {
                p = N6887e7e936(i)
            } else if ((i[0] as Double?)!!.toDouble() > 249.826761) {
                p = 1.0
            }
            return p
        }

        fun N6887e7e936(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[2] == null) {
                p = 1.0
            } else if ((i[2] as Double?)!!.toDouble() <= 32.282655) {
                p = 1.0
            } else if ((i[2] as Double?)!!.toDouble() > 32.282655) {
                p = 2.0
            }
            return p
        }

        fun N67fed1af37(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[5] == null) {
                p = 1.0
            } else if ((i[5] as Double?)!!.toDouble() <= 21.531063) {
                p = 1.0
            } else if ((i[5] as Double?)!!.toDouble() > 21.531063) {
                p = N60d1266f38(i)
            }
            return p
        }

        fun N60d1266f38(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[5] == null) {
                p = 2.0
            } else if ((i[5] as Double?)!!.toDouble() <= 37.582128) {
                p = N8bcebea39(i)
            } else if ((i[5] as Double?)!!.toDouble() > 37.582128) {
                p = N4e312c5741(i)
            }
            return p
        }

        fun N8bcebea39(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[13] == null) {
                p = 1.0
            } else if ((i[13] as Double?)!!.toDouble() <= 3.582585) {
                p = 1.0
            } else if ((i[13] as Double?)!!.toDouble() > 3.582585) {
                p = N5f53fdbb40(i)
            }
            return p
        }

        fun N5f53fdbb40(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[2] == null) {
                p = 2.0
            } else if ((i[2] as Double?)!!.toDouble() <= 112.968839) {
                p = 2.0
            } else if ((i[2] as Double?)!!.toDouble() > 112.968839) {
                p = 1.0
            }
            return p
        }

        fun N4e312c5741(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[5] == null) {
                p = 1.0
            } else if ((i[5] as Double?)!!.toDouble() <= 60.431559) {
                p = 1.0
            } else if ((i[5] as Double?)!!.toDouble() > 60.431559) {
                p = N273a99a042(i)
            }
            return p
        }

        fun N273a99a042(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[2] == null) {
                p = 1.0
            } else if ((i[2] as Double?)!!.toDouble() <= 92.030773) {
                p = 1.0
            } else if ((i[2] as Double?)!!.toDouble() > 92.030773) {
                p = 2.0
            }
            return p
        }

        fun N4836d51643(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[9] == null) {
                p = 2.0
            } else if ((i[9] as Double?)!!.toDouble() <= 19.244263) {
                p = N848478e44(i)
            } else if ((i[9] as Double?)!!.toDouble() > 19.244263) {
                p = 1.0
            }
            return p
        }

        fun N848478e44(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[2] == null) {
                p = 1.0
            } else if ((i[2] as Double?)!!.toDouble() <= 21.427157) {
                p = 1.0
            } else if ((i[2] as Double?)!!.toDouble() > 21.427157) {
                p = 2.0
            }
            return p
        }

        fun N4fdc914445(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[64] == null) {
                p = 1.0
            } else if ((i[64] as Double?)!!.toDouble() <= 14.465496) {
                p = N3c58f68646(i)
            } else if ((i[64] as Double?)!!.toDouble() > 14.465496) {
                p = N42380a7e47(i)
            }
            return p
        }

        fun N3c58f68646(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[17] == null) {
                p = 1.0
            } else if ((i[17] as Double?)!!.toDouble() <= 5.179447) {
                p = 1.0
            } else if ((i[17] as Double?)!!.toDouble() > 5.179447) {
                p = 2.0
            }
            return p
        }

        fun N42380a7e47(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[14] == null) {
                p = 2.0
            } else if ((i[14] as Double?)!!.toDouble() <= 15.764529) {
                p = N886518048(i)
            } else if ((i[14] as Double?)!!.toDouble() > 15.764529) {
                p = N5a0513c351(i)
            }
            return p
        }

        fun N886518048(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[3] == null) {
                p = 1.0
            } else if ((i[3] as Double?)!!.toDouble() <= 36.599112) {
                p = N237bb76549(i)
            } else if ((i[3] as Double?)!!.toDouble() > 36.599112) {
                p = 2.0
            }
            return p
        }

        fun N237bb76549(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[20] == null) {
                p = 1.0
            } else if ((i[20] as Double?)!!.toDouble() <= 5.519299) {
                p = N586c894a50(i)
            } else if ((i[20] as Double?)!!.toDouble() > 5.519299) {
                p = 2.0
            }
            return p
        }

        fun N586c894a50(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[10] == null) {
                p = 2.0
            } else if ((i[10] as Double?)!!.toDouble() <= 3.447845) {
                p = 2.0
            } else if ((i[10] as Double?)!!.toDouble() > 3.447845) {
                p = 1.0
            }
            return p
        }

        fun N5a0513c351(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[0] == null) {
                p = 1.0
            } else if ((i[0] as Double?)!!.toDouble() <= 657.152274) {
                p = N6035435252(i)
            } else if ((i[0] as Double?)!!.toDouble() > 657.152274) {
                p = 2.0
            }
            return p
        }

        fun N6035435252(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 2.0
            } else if ((i[1] as Double?)!!.toDouble() <= 74.336459) {
                p = 2.0
            } else if ((i[1] as Double?)!!.toDouble() > 74.336459) {
                p = 1.0
            }
            return p
        }

        fun N1e8b10ae53(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[64] == null) {
                p = 1.0
            } else if ((i[64] as Double?)!!.toDouble() <= 16.388805) {
                p = N2291ecec54(i)
            } else if ((i[64] as Double?)!!.toDouble() > 16.388805) {
                p = 2.0
            }
            return p
        }

        fun N2291ecec54(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[8] == null) {
                p = 1.0
            } else if ((i[8] as Double?)!!.toDouble() <= 5.277144) {
                p = 1.0
            } else if ((i[8] as Double?)!!.toDouble() > 5.277144) {
                p = N5078787655(i)
            }
            return p
        }

        fun N5078787655(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 1.0
            } else if ((i[1] as Double?)!!.toDouble() <= 68.263823) {
                p = N1a9d8ef856(i)
            } else if ((i[1] as Double?)!!.toDouble() > 68.263823) {
                p = 2.0
            }
            return p
        }

        fun N1a9d8ef856(i: DoubleArray): Double {
            var p = Double.NaN
            if (i[1] == null) {
                p = 2.0
            } else if ((i[1] as Double?)!!.toDouble() <= 51.296459) {
                p = 2.0
            } else if ((i[1] as Double?)!!.toDouble() > 51.296459) {
                p = 1.0
            }
            return p
        }
    }
}