package com.example.artun_cimensel_myruns4.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.database.*
import com.example.artun_cimensel_myruns4.fragments.StartFragment
import com.example.artun_cimensel_myruns4.services.MapLocationService
import com.example.artun_cimensel_myruns4.services.MapServiceViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*
import kotlin.properties.Delegates


class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object{
        const val KEY_ENTRY = "entry_type"
        const val KEY_INPUT = "input_type"
        const val KEY_ACTIVITY = "activity_type"
        const val KEY_CALORIES = "calories"
        const val KEY_CLIMB = "climb"
        const val KEY_AVG_SPEED = "avg_speed"
        const val KEY_DISTANCE = "distance"
        const val KEY_LIST = "list"
    }

    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: HistoryDatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyEntry: HistoryEntry

    private lateinit var textView: TextView

    private var displayWhenMapReady = false
    private var entryIsNew by Delegates.notNull<Boolean>()
    private var input by Delegates.notNull<Int>()
    private var activity by Delegates.notNull<Int>()

    private lateinit var appContext: Context
    private var isBound = false
    private lateinit var mapServiceViewModel: MapServiceViewModel
    private lateinit var intentService: Intent

    private lateinit var mMap: GoogleMap
    private var mapCentered = false
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions
    private lateinit var  polylines: ArrayList<Polyline>

    val kyiv = LatLng(50.4501, 30.5234) //kyiv
    val warsaw = LatLng(52.2297, 21.0122) //warsaw
    val bucharest = LatLng(44.4268, 26.1025) //bucharest
    private val locationBroadcastReceiver = LocationBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        database = HistoryDatabase.getInstance(this)
        databaseDao = database.historyDatabaseDao
        repository = HistoryRepository(databaseDao)
        viewModelFactory = HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory).get(HistoryViewModel::class.java)

        textView = findViewById<TextView>(R.id.type_stats)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        entryIsNew = intent.getBooleanExtra(KEY_ENTRY, false)
        input = intent.getIntExtra(KEY_INPUT, 0)
        activity = intent.getIntExtra(KEY_ACTIVITY, 0)

        if(!entryIsNew){
            findViewById<Button>(R.id.btn_delete).visibility = View.GONE

            //TODO should be entered in correct units eventually when getting actual values
            historyEntry = HistoryEntry()
            historyEntry.inputType = input
            historyEntry.activityType = activity
            historyEntry.avgSpeed = 8.01 // km or mile per hour
            historyEntry.climb = 10.0 // km or mile
            historyEntry.calorie = 1.0
            historyEntry.distance = 0.03 // km or mile
            historyEntry.duration = 20.0 //min

            val locationList = ArrayList<LatLng>()
            locationList.add(kyiv) //kyiv
            locationList.add(warsaw) //warsaw
            locationList.add(bucharest) //bucharest
            historyEntry.locationList = locationList


            intentService = Intent(this, MapLocationService::class.java)
            appContext = this.applicationContext
            mapServiceViewModel = ViewModelProvider(this).get(MapServiceViewModel::class.java)


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            startService()
        }
        else{
            changeUIForOldEntryPoints()
            displayWhenMapReady = true;
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d("DEBUG: ", "MAP IS READY")
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.BLACK)
        polylines = ArrayList()
        markerOptions = MarkerOptions()
        if(displayWhenMapReady)
            displayOldEntryPoints()
    }

    private fun displayOldEntryPoints(){
        try {
            val list = intent.getSerializableExtra(KEY_LIST) as ArrayList<LatLng>

            for(i in list.indices){
                if(i == 0)
                    mMap.addMarker(MarkerOptions().position(list[i]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                else if(i == list.size - 1){
                    mMap.addMarker(MarkerOptions().position(list[i]))

                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(list[i], 17f)
                    mMap.animateCamera(cameraUpdate) // centers and zooms into location
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng)) // centers but doesn't zoom
                }

                polylineOptions.add(list[i])
            }
            polylines.add(mMap.addPolyline(polylineOptions))
        }
        catch (e: Exception) {
            Log.d("DEBUG: ", "displayOldEntryPoints exception: ${e.message}")
        }
    }

    private fun changeUIForOldEntryPoints(){
        findViewById<LinearLayout>(R.id.buttons).visibility = View.GONE

        val avgSpeed = intent.getStringExtra(KEY_AVG_SPEED)
        val climb = intent.getStringExtra(KEY_CLIMB)
        val calories = intent.getDoubleExtra(KEY_CALORIES, 0.0)
        val distance = intent.getStringExtra(KEY_DISTANCE)

        val str = "Type: ${StartFragment.activityTypes[activity]}\nAvg Speed: ${avgSpeed}\nCurr speed: n/a\nClimb: ${climb}\nCalorie: ${calories}\nDistance: $distance"
        textView.text = str
    }

    override fun onPause() {
        if(entryIsNew)
            unregisterReceiver(locationBroadcastReceiver)

        super.onPause()
    }

    override fun onResume() {
        if(entryIsNew) {
            val intentFilter = IntentFilter()
            intentFilter.addAction("broadcast_location")
            registerReceiver(locationBroadcastReceiver, intentFilter)
        }

        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()

        stopServiceFun()
    }

    private fun startService(){
        startService(intentService)

        if (!isBound) {
            appContext.bindService(intentService, mapServiceViewModel, Context.BIND_AUTO_CREATE)
            isBound = true
        }
    }

    private fun stopServiceFun(){
        if (isBound) {
            appContext.unbindService(mapServiceViewModel)
            isBound = false
        }
        stopService(intentService)
    }

    fun onSaveClicked(view: View) {
        historyEntry.dateTime = Calendar.getInstance()
        historyViewModel.insert(historyEntry)
        finish()
    }

    fun onCancelClicked(view: View) {
        stopServiceFun()
        finish()
    }

    override fun onBackPressed() {
        stopServiceFun()
        super.onBackPressed()
    }

    fun deleteEntry(view: View) {
        val returnIntent = Intent()
        returnIntent.putExtra("return_index", intent.getIntExtra("list_index", -1))
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("DEBUG:", "____________________onReceive")
        }
    }
}