package com.example.myruns4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import org.w3c.dom.Comment

class EntryView : AppCompatActivity() {

    private lateinit var database: ManualDatabase
    private lateinit var databaseDao: ManualDatabaseDao
    private lateinit var repository: ManualRepository
    private lateinit var viewModelFactory: ManualViewModelFactory
    private lateinit var manualViewModel: ManualViewModel

    private lateinit var myViewModel: MyViewModel

    private var id: Long = 0
    private var input: String = ""
    private var type: String = ""
    private var distance_value: String = " "
    private var duration_value: String = " "
    private var calories_value: String = " "
    private var heartRate_value: String = " "
    private var date: String = " "
    private var time: String = " "
    private var comment_value: String = " "
    private var unit: String = ""

    private lateinit var inputType : EditText
    private lateinit var activityType : EditText
    private lateinit var date_time : EditText
    private lateinit var duration : EditText
    private lateinit var distance : EditText
    private lateinit var calories : EditText
    private lateinit var heartRate : EditText
    private lateinit var comment : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_view)

        database = ManualDatabase.getInstance(this)
        databaseDao = database.manualDatabaseDao
        repository = ManualRepository(databaseDao)
        viewModelFactory = ManualViewModelFactory(repository)
        manualViewModel = ViewModelProvider(this, viewModelFactory).get(ManualViewModel::class.java)
        manualViewModel.allManualsLiveData.observe(this, Observer { it ->

        })

        //Checking which unit is selected by the user
        val currValue = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("pref_units", "Metric (Kilometers)")

        val extras = intent.extras
        id = extras?.getLong("id")!!
        input = extras.getString("input")!!
        type = extras.getString("type")!!
        date = extras.getString("date")!!
        time = extras.getString("time")!!
        distance_value = extras.getString("distance")!!
        duration_value = extras.getString("duration")!!
        heartRate_value = extras.getString("heart_rate")!!
        calories_value = extras.getString("calories")!!
        comment_value = extras.getString("comment")!!

        inputType = findViewById(R.id.input)
        activityType = findViewById(R.id.activity)
        duration = findViewById(R.id.durationInput)
        distance = findViewById(R.id.distanceInput)
        date_time = findViewById(R.id.date)
        calories = findViewById(R.id.caloriesInput)
        heartRate = findViewById(R.id.heartRateInput)
        comment = findViewById(R.id.commentInput)

        if(currValue == "Metric (Kilometers)")
        {
            unit = "Kilometers"
        }
        else
            unit = "Miles"

        //Setting the value for the editText for display database
        inputType.setText(input)
        activityType.setText(type)
        date_time.setText("$time $date")
        duration.setText("$duration_value secs")
        distance.setText("$distance_value $unit")
        calories.setText("$calories_value cals")
        heartRate.setText("$heartRate_value bpm")
        comment.setText(comment_value)

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