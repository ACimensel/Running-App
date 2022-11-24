package com.example.artun_cimensel_myruns4.services

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.example.artun_cimensel_myruns4.activities.MapActivity
import com.example.artun_cimensel_myruns4.database.HistoryEntry
import com.google.android.gms.maps.model.LatLng


class MapLocationService: Service(), LocationListener {
    private lateinit var myBinder: MyBinder
    private lateinit var locationManager: LocationManager
    private val entry = HistoryEntry()
    private var started = false


    override fun onCreate() {
        super.onCreate()
        Log.d("DEBUG", "____________________Service onCreate() called____________________")
        myBinder = MyBinder()

        entry.inputType = 1
        entry.activityType = 0 // TODO change this
        entry.locationList = ArrayList<LatLng>()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("DEBUG", "__________Service onStartCommand() called everytime startService() is called; startId: $startId flags: $flags")

        return START_NOT_STICKY
    }

    //XD:Multiple clients can connect to the service at once. However, the system calls your
    // service's onBind() method to retrieve the IBinder only when the first client binds.
    // The system then delivers the same IBinder to any additional clients that bind, without
    // calling onBind() again.
    override fun onBind(intent: Intent?): IBinder? {
        Log.d("DEBUG", "__________Service onBind() called")
        if(!started) initLocationManager()

        return myBinder
    }

    //XD: return false will allow you to unbind only once. Play with it.
    //XD: Return true if you would like to have the service's onRebind(Intent) method later called
    // when new clients bind to it.
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d("DEBUG", "__________Service onUnBind() called")
        return true
    }

    override fun onDestroy() {
        Log.d("DEBUG", "__________Service onDestroy() called")
        locationManager.removeUpdates(this)

        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d("DEBUG", "__________app removed from the application list")
        stopSelf()
    }

    inner class MyBinder : Binder() {

    }

    private fun initLocationManager() {
        started = true
        Log.d("DEBUG", "__________Service initLocationManager() called")
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            criteria.powerRequirement = Criteria.POWER_LOW
            criteria.isAltitudeRequired = true
            criteria.isSpeedRequired = true

            val provider: String? = locationManager.getBestProvider(criteria, true)
            Log.d("DEBUG: ", "__________provider $provider")

            if(provider != null)
                locationManager.requestLocationUpdates(provider, 5000, 0f, this@MapLocationService)
        }
        catch (e: SecurityException) {
            Log.d("DEBUG: ", "__________initLocationManager exception")
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d("DEBUG", "__________Service onLocationChanged() called")
        try {
            val lat = location.latitude
            val lng = location.longitude
            val latLng = LatLng(lat, lng)

            entry.locationList?.add(latLng)

            Toast.makeText(this, latLng.toString() + " ${entry.locationList?.size}", Toast.LENGTH_SHORT).show()
            Log.d("DEBUG", "__________${latLng}")

            //TODO send signal, receive in mapactivity
            val intent = Intent()
            intent.action = "broadcast_location"
            sendBroadcast(intent)
        }
        catch (e: Exception) {
            Log.d("DEBUG: ", "__________onLocationChanged exception: $e")
        }
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
}