package com.example.myruns4

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.myruns4.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var manualViewModel: ManualViewModel
    private lateinit var database: ManualDatabase
    private lateinit var databaseDao: ManualDatabaseDao
    private lateinit var repository: ManualRepository
    private lateinit var viewModelFactory: ManualViewModelFactory
    var id by Delegates.notNull<Long>()

    private val PERMISSION_REQUEST_CODE = 0
    private var mapCentered = false
    private lateinit var locationList: ArrayList<LatLng>
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions
    private lateinit var  polylines: ArrayList<Polyline>
    private var lastMarker : Marker? = null

    private lateinit var serviceIntent : Intent
    private lateinit var mapViewModel: MapViewModel
    private var isBind: Boolean = false


    private lateinit var activityTypeTV: TextView
    private lateinit var averageSpeedTV: TextView
    private lateinit var currentSpeedTV: TextView
    private lateinit var climbTV: TextView
    private lateinit var caloriesTV: TextView
    private lateinit var distanceTV: TextView
    private lateinit var units: String



    private var inputType: String = ""
    private var activityType: String = ""
    private var dateTime: String = ""
    private var time_val: String = ""
    private val calendar = Calendar.getInstance()
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var avgPace: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var calories: Double = 0.0
    private var climb: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        serviceIntent = Intent(this, TrackingService::class.java)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        locationList = ArrayList()
        activityTypeTV = findViewById(R.id.map_activity_type)
        averageSpeedTV = findViewById(R.id.map_average_speed)
        currentSpeedTV = findViewById(R.id.map_current_speed)
        climbTV = findViewById(R.id.map_climb)
        caloriesTV = findViewById(R.id.map_calorie)
        distanceTV = findViewById(R.id.map_distance)


        units = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("pref_units", "Metric (Kilometers)").toString()

        //Getting the Date
        val temp_format = SimpleDateFormat("HH:mm:ss MM/dd/yyyy")
        val time = android.icu.util.Calendar.getInstance().time
        val formatter = SimpleDateFormat.getDateInstance()
        dateTime = formatter.format(time)

        //Getting the Time
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        var minutes = calendar.get(Calendar.MINUTE)
        var seconds = calendar.get(Calendar.SECOND)
        time_val = "$hour:$minutes:$seconds"


        database = ManualDatabase.getInstance(this)
        databaseDao = database.manualDatabaseDao
        repository = ManualRepository(databaseDao)
        viewModelFactory = ManualViewModelFactory(repository)
        manualViewModel = ViewModelProvider(this, viewModelFactory).get(ManualViewModel::class.java)
        manualViewModel.allManualsLiveData.observe(this, { it ->

        })

                var bundle: Bundle? = intent.extras
                activityType = bundle?.getString("input_type")!!
                inputType = bundle?.getString("entry_type")!!
                if(inputType == "Automatic")
                {
                    activityType = "Unknown"

                    mapViewModel.automatic_activity.observe(this)
                    {
                        activityTypeTV.text = "Type: $it"
                        when(it.toString())
                        {
                            "Running" ->{activityType = "Running"}
                            "Walking" ->{activityType = "Walking"}
                            "Standing" ->{activityType = "Standing"}
                            "Other" ->{activityType = "Other"}
                        }
                    }
                }
                else
                {
                    activityType = bundle?.getString("input_type")!!
                }


                activityTypeTV.text = "Type: $activityType"


    }

    //when map is ready start tracking
    override fun onMapReady(googleMap: GoogleMap)
    {
        mMap = googleMap
        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        polylines = ArrayList()

            checkPermission()
            mapViewModel.bundle.observe(this)
            {
                mapOut(it)
            }

    }

    //Check if the app has permissions
    private fun checkPermission()
    {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_REQUEST_CODE)
        }
        else
        {
            startTrackService()
        }
    }

    private fun startTrackService()
    {
        applicationContext.startService(serviceIntent)
        if (!isBind)
        {
            applicationContext.bindService(serviceIntent, mapViewModel, Context.BIND_AUTO_CREATE)
            isBind = true
        }
    }

    private fun mapOut(bundle: Bundle)
    {
        //Get the location data from service.
        locationList = toArrayList(bundle.getString("LOCATION")!!)!!
        if (locationList.isEmpty())
        {
            mMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
        }
        else
        {
            duration = bundle.getDouble("DURATION")
            distance = bundle.getDouble("DISTANCE")
            avgPace = bundle.getDouble("CURRENT_SPEED")
            avgSpeed = bundle.getDouble("AVERAGE_SPEED")
            calories = bundle.getDouble("CALORIES")
            climb = bundle.getDouble("CLIMB")

            changeMapData()


            polylineOptions = PolylineOptions()
            polylineOptions.addAll(locationList)
            mMap.addPolyline(polylineOptions)

            if (!mapCentered)
            {
                val latLng = locationList.first()
                markerOptions.position(latLng).title("Start location")
                mMap.addMarker(markerOptions)
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20f)
                mMap.animateCamera(cameraUpdate)
                mapCentered = true
            }

            val latLng = locationList.last()
            lastMarker?.remove()
            markerOptions.position(latLng).title("End location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            lastMarker = mMap.addMarker(markerOptions)
        }
    }

    //Change the stats according to the units selected by the user.
    private fun changeMapData()
    {
        if (units == "Imperial (Miles)")
        {

            averageSpeedTV.text = "Avg speed: ${String.format("%.1f" ,avgSpeed)} m/h"
            currentSpeedTV.text = "Cur speed: ${String.format("%.1f" ,avgPace)} m/h"
            climbTV.text = "Climb: ${String.format("%.1f" ,climb)} Miles"
            caloriesTV.text = "Calorie: ${String.format("%.1f" ,calories)}"
            distanceTV.text = "Distance: ${String.format("%.3f" ,distance)} Miles"
        }
        else
        {
            averageSpeedTV.text = "Avg speed: ${String.format("%.1f" ,(avgSpeed * 1.61))} km/h"
            currentSpeedTV.text = "Cur speed: ${String.format("%.1f" ,avgPace * 1.61)} km/h"
            climbTV.text = "Climb: ${String.format("%.1f" ,climb * 1.61)} Kilometers"
            caloriesTV.text = "Calorie: ${String.format("%.1f" ,calories)}"
            distanceTV.text = "Distance: ${String.format("%.3f" ,distance * 1.61)} Kilometers"
        }
    }

    fun toArrayList(json: String): ArrayList<LatLng>?
    {
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return Gson().fromJson(json, listType)

    }


    fun fromArrayList(array: ArrayList<LatLng>?): String
    {
        return Gson().toJson(array)
    }

    //Saving the GPS data to database
    fun mapOnSaveClicked(view: View)
    {
        var manual = Manual()
        manual.Type = inputType
        manual.Input = activityType
        manual.Duration = duration
        manual.Distance = distance
        manual.Date = dateTime
        manual.Time = time_val
        manual.Heart_Rate = 0
        manual.Calories = calories
        manual.Comment = ""
        manual.Avg_Pace = avgPace
        if (avgPace.isNaN())
        {
            manual.Avg_Pace = 0.0
        }
        manual.Avg_Speed = avgSpeed
        if (avgSpeed.isNaN())
        {
            manual.Avg_Speed = 0.0
        }
        manual.locations = locationList
        manualViewModel?.insert(manual)
        if(manualViewModel.allManualsLiveData.value?.isEmpty() == true)
            Toast.makeText(this, "Entry #0 saved.", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Entry #${manualViewModel.allManualsLiveData.value?.last()?.id} saved.", Toast.LENGTH_SHORT).show()

        finish()
    }

    //Cancelling the GPS entry
    fun mapOnCancelClicked(view: View)
    {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    // when activity is closed, unbind and stop service
    override fun finish()
    {
        super.finish()
        if (isBind)
        {
            applicationContext.unbindService(mapViewModel)
            stopService(serviceIntent)
            isBind = false
        }
    }

}

