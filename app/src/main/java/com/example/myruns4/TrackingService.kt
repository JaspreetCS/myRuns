package com.example.myruns4

import android.app.*
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.util.concurrent.ArrayBlockingQueue

import kotlin.collections.ArrayList
import kotlin.math.sqrt


/**
 * gets location, calculates speeds and other stats, sends bundle to MapViewModel
 */

class TrackingService: Service(), LocationListener, SensorEventListener
{
    //About notification
    private lateinit var notificationManager: NotificationManager
    private val CHANNEL_ID = "notification channel"
    private val NOTIFICATION_ID = 1

    //About location
    private lateinit var locationManager: LocationManager
    private lateinit var locationList : ArrayList<LatLng>
    private lateinit var lastLocation : Location

    //About message
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var curSpeed: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var calories: Double = 0.0
    private var climb: Double = 0.0

    private var startTime : Long = 0L
    private var lastTime : Long = 0L

    //About bind
    private lateinit var  myBinder: MyBinder
    private var msgHandler: Handler? = null

    private lateinit var sensorManager : SensorManager
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    private var stopThread = false

    companion object
    {
        val MSG = 0
        val AUTO_MSG = 1
    }

    val automatic_activity_array = arrayOf("Standing", "Walking", "Running", "Other")

    override fun onCreate()
    {
        super.onCreate()
        showNotification()

        myBinder = MyBinder()

        locationList = ArrayList()
        initLocationManager()

        startTime = System.currentTimeMillis()

        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        object: Thread()
        {
            override fun run()
            {
                super.run()
                startAutomatic()
            }
        }.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        return START_NOT_STICKY
    }

    //Show notification when GPS is in use
    private fun showNotification()
    {
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_background)
        notificationBuilder.setContentTitle("MyRuns")
        notificationBuilder.setContentText("Recording your path now")

        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        notificationBuilder.setContentIntent(pendingIntent)

        val notification = notificationBuilder.build()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26)
        {
            val notificationChannel = NotificationChannel(CHANNEL_ID, "service-channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun initLocationManager()
    {
        try
        {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val provider : String? = locationManager.getBestProvider(criteria, true)
            if(provider != null)
            {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null)
                    onLocationChanged(location)
                locationManager.requestLocationUpdates(provider, 0, 0f, this)
            }
        }
        catch (e: SecurityException)
        {
            println("debug:LocationManager")
        }
    }

    //when location changes, do calculations for everything, then send message notifying update
    override fun onLocationChanged(location: Location)
    {
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        locationList.add(latLng)

        if (::lastLocation.isInitialized)
        {
            locationList.add(latLng)

            val currentTime = System.currentTimeMillis()
            duration = ((currentTime - startTime) / 1000).toDouble()

            // meters to miles = 1609.344F
            distance += lastLocation.distanceTo(location) / 1609.344F

            curSpeed = (0 until 40).random().toDouble()/2

            avgSpeed = distance / (duration / 3600)

            calories = (distance * 120)

            climb = (lastLocation.altitude - location.altitude) / 1000
        }

        lastLocation = location
        lastTime = System.currentTimeMillis()

        sendMessage()
    }

    override fun onBind(p0: Intent?): IBinder?
    {
        return myBinder
    }

    inner class MyBinder : Binder()
    {
        fun setmsgHandler(msgHandler: Handler)
        {
            this@TrackingService.msgHandler = msgHandler
        }
    }

    override fun onUnbind(intent: Intent?): Boolean
    {
        msgHandler = null
        return true
    }

    //format message with data to send to mapview model
    private fun sendMessage()
    {
            if(msgHandler != null)
            {
                val bundle = Bundle()
                bundle.putDouble("DURATION", duration)
                bundle.putDouble("DISTANCE", distance)
                bundle.putDouble("CURRENT_SPEED", curSpeed)
                bundle.putDouble("AVERAGE_SPEED", avgSpeed)
                bundle.putDouble("CALORIES", calories)
                bundle.putDouble("CLIMB", climb)
                bundle.putString("LOCATION", fromArrayList(locationList))
                val message = msgHandler!!.obtainMessage()
                message.data = bundle
                message.what = MSG
                msgHandler!!.sendMessage(message)
            }
    }


    fun fromArrayList(array: ArrayList<LatLng>?): String
    {
        return Gson().toJson(array)
    }

    //for when app is closed, stop service
    override fun onTaskRemoved(rootIntent: Intent?)
    {
        super.onTaskRemoved(rootIntent)
        notificationManager.cancel(NOTIFICATION_ID)
        if (locationManager != null)
        {
            locationManager.removeUpdates(this)
        }
        locationList.clear()
        sensorManager.unregisterListener(this)
        stopThread = true
        stopSelf()
    }


    override fun onDestroy()
    {
        super.onDestroy()
        msgHandler = null
        notificationManager.cancel(NOTIFICATION_ID)
        if (locationManager != null)
        {
            locationManager.removeUpdates(this)
        }
        locationList.clear()
        sensorManager.unregisterListener(this)
        stopThread = true
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(event: SensorEvent?)
    {
        if (event != null && event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            val m = Math.sqrt(((event.values[0] * event.values[0]) + (event.values[1] * event.values[1]) + (event.values[2]
                    * event.values[2])).toDouble())

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

    //To track activity
    private fun startAutomatic()
    {
        // Creating a vector for tracking the activity
        val vector = ArrayList<Double>(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        var blockSize = 0
        val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
        var max = Double.MIN_VALUE
        while (true)
        {
            try
            {
                // Dumping buffer
                accBlock[blockSize++] = mAccBuffer.take().toDouble()
                if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                    blockSize = 0

                    // time = System.currentTimeMillis();
                    max = .0
                    for (`val` in accBlock)
                    { //find max value
                        if (max < `val`)
                        {
                            max = `val`
                        }
                    }
                    fft.fft(accBlock, im)
                    for (i in accBlock.indices)
                    {
                        val mag = sqrt(accBlock[i] * accBlock[i] + im[i] * im[i])
                        im[i] = .0 // Clear the field
                        vector.add(mag)
                    }
                    vector.add(max)

                    //Using Weka to send message
                    val weka_value = WekaClassifier.classify(vector.toArray()).toInt()
                    sendAutoMessage(automatic_activity_array[weka_value])
                    vector.clear()
                }
            }
            catch (e: Exception)
            { e.printStackTrace() }

            if (stopThread) {
                break
            }
        }
    }

    //auto_activity message to send to mapview model
    private fun sendAutoMessage(weka_value: String)
    {
            if(msgHandler != null)
            {
                val bundle = Bundle()
                bundle.putString("AUTO_ACTIVITY", weka_value)
                val message = msgHandler!!.obtainMessage()
                message.data = bundle
                message.what = AUTO_MSG
                msgHandler!!.sendMessage(message)
            }
    }
}