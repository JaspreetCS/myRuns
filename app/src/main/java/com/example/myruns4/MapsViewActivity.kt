package com.example.myruns4

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.myruns4.databinding.ActivityMapsViewBinding
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MapsViewActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsViewBinding
    private var mapCentered = false
    private lateinit var locationList: ArrayList<LatLng>
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions
    private lateinit var  polylines: ArrayList<Polyline>
    private var lastMarker : Marker? = null

    private lateinit var activityType : TextView
    private lateinit var avg_speed : TextView
    private lateinit var distance : TextView
    private lateinit var calories : TextView
    private lateinit var cur_speed : TextView
    private lateinit var climb : TextView

    private lateinit var database: ManualDatabase
    private lateinit var databaseDao: ManualDatabaseDao
    private lateinit var repository: ManualRepository
    private lateinit var viewModelFactory: ManualViewModelFactory
    private lateinit var manualViewModel: ManualViewModel

    private var id: Long = 0
    private lateinit var locationlist: ArrayList<LatLng>
    private var type: String = ""
    private var input: String = ""
    private var distance_value: String = " "

    private var calories_value: String = " "
    private var climb_value: String = " "
    private var avg_speed_value: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        database = ManualDatabase.getInstance(this)
        databaseDao = database.manualDatabaseDao
        repository = ManualRepository(databaseDao)
        viewModelFactory = ManualViewModelFactory(repository)
        manualViewModel = ViewModelProvider(this, viewModelFactory).get(ManualViewModel::class.java)
        manualViewModel.allManualsLiveData.observe(this, Observer { it ->

        })

        //Get the entry data to display
        locationlist = ArrayList()
        val extras = intent.extras
        id = extras?.getLong("id")!!
        input = extras.getString("type")!!
        type = extras.getString("input")!!
        distance_value = extras.getString("distance")!!
        climb_value = extras.getString("climb")!!
        calories_value = extras.getString("calories")!!
        avg_speed_value = extras.getDouble("avg_speed")!!
        locationList = toArrayList(extras.getString("locations")!!)!!

        //Stat data text views
        activityType = findViewById(R.id.map_activity_type)
        distance = findViewById(R.id.map_distance)
        avg_speed = findViewById(R.id.map_average_speed)
        calories = findViewById(R.id.map_calorie)
        cur_speed = findViewById(R.id.map_current_speed)
        climb = findViewById(R.id.map_climb)



        //Checking which unit is selected by the user
        val currValue = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("pref_units", "Metric (Kilometers)")

        activityType.text = "Type: $type"
        calories.text = "Calorie: ${String.format("%.1f",calories_value.toDouble())}"
        cur_speed.text = "Cur speed: N/A"
        if(currValue == "Metric (Kilometers)")
        {
            avg_speed.text = "Avg speed: ${String.format("%.1f" ,avg_speed_value)} km/h"
            climb.text = "Climb: $climb_value Kilometers"
            distance.text = "Distance: ${String.format("%.3f" ,distance_value.toDouble())} Kilometers"
        }
        else {
            avg_speed.text = "Avg speed: ${String.format("%.1f" ,avg_speed_value)} m/h"
            climb.text = "Climb: $climb_value Miles"
            distance.text = "Distance: ${String.format("%.3f" ,distance_value.toDouble())} Miles"
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    // Load the location data on map to show the stored path
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        markerOptions = MarkerOptions()
        polylineOptions = PolylineOptions()
        polylineOptions.color(Color.RED)
        polylines = ArrayList()

        if (locationList.isEmpty())
        {
            mMap.addMarker(MarkerOptions().position(LatLng(0.0, 0.0)))
        }
        else {
            polylineOptions = PolylineOptions()
            polylineOptions.addAll(locationList)
            mMap.addPolyline(polylineOptions)

            if (!mapCentered) {
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

    fun toArrayList(json: String): ArrayList<LatLng>?
    {
        val listType: Type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return Gson().fromJson(json, listType)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.delete, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.action_delete -> {
            manualViewModel.delete(id)
            finish()
            System.out.close()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}