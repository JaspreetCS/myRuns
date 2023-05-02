package com.example.myruns4

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.myruns4.ManualDatabase
import com.example.myruns4.ManualDatabaseDao
import com.example.myruns4.ManualRepository
import kotlinx.coroutines.Runnable
import java.text.SimpleDateFormat

class Start_Button : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var listView: ListView
    private val START = arrayOf("Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment")
    private val calendar = Calendar.getInstance()

    private lateinit var database: ManualDatabase
    private lateinit var databaseDao: ManualDatabaseDao
    private lateinit var repository: ManualRepository
    private lateinit var viewModelFactory: ManualViewModelFactory
    private lateinit var manualViewModel: ManualViewModel


    private var type = ""
    private var input = ""
    private var date = ""
    private var time = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_button)

        var bundle: Bundle? = intent.extras
        input = bundle?.getString("entry_type")!!
        type = bundle?.getString("input_type")!!


        listView = findViewById(R.id.start)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,START)
        listView.adapter = arrayAdapter

        database = ManualDatabase.getInstance(this)
        databaseDao = database.manualDatabaseDao
        repository = ManualRepository(databaseDao)
        viewModelFactory = ManualViewModelFactory(repository)
        manualViewModel = ViewModelProvider(this, viewModelFactory).get(ManualViewModel::class.java)

        manualViewModel.allManualsLiveData.observe(this,  { it ->

        })

        listView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String

            if (selectedItem == "Date"){
                val datePickerDialog = DatePickerDialog(this, this, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH))
                datePickerDialog.show()
            }
            else if (selectedItem == "Time"){
                val timePickerDialog = TimePickerDialog(this, this, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),true)
                timePickerDialog.show()
            }
            else if (selectedItem == "Duration"){
                val inputDialog = InputDialog()
                val bundle = Bundle()
                bundle.putInt(InputDialog.DIALOG_KEY, InputDialog.DURATION)
                inputDialog.arguments = bundle
                inputDialog.show(supportFragmentManager, "input dialog")

            }
            else if (selectedItem == "Distance"){
                val inputDialog = InputDialog()
                val bundle = Bundle()
                bundle.putInt(InputDialog.DIALOG_KEY, InputDialog.DISTANCE)
                inputDialog.arguments = bundle
                inputDialog.show(supportFragmentManager, "input dialog")

            }
            else if (selectedItem == "Calories"){
                val inputDialog = InputDialog()
                val bundle = Bundle()
                bundle.putInt(InputDialog.DIALOG_KEY, InputDialog.CALORIES)
                inputDialog.arguments = bundle
                inputDialog.show(supportFragmentManager, "input dialog")
            }
            else if (selectedItem == "Heart Rate"){
                val inputDialog = InputDialog()
                val bundle = Bundle()
                bundle.putInt(InputDialog.DIALOG_KEY, InputDialog.HEART_RATE)
                inputDialog.arguments = bundle
                inputDialog.show(supportFragmentManager, "input dialog")

            }
            else {
                val inputDialog = InputDialog()
                val bundle = Bundle()
                bundle.putInt(InputDialog.DIALOG_KEY, InputDialog.COMMENT)
                inputDialog.arguments = bundle
                inputDialog.show(supportFragmentManager, "input dialog")
            }
         }
        }

    fun onCancelEntry(view: View) {
        Toast.makeText(this, "Entry discarded.", Toast.LENGTH_SHORT).show()
        finish()
        System.out.close()
    }

    fun onSaveEntry(view: View) {

        val thread = Thread(){
            val runnable = Runnable(){
                addEntry()
            }
            val handler = Handler(Looper.getMainLooper())
            handler.post(runnable)
        }
        thread.start()
        finish()
        System.out.close()
    }

    fun addEntry(){

        /*
        Creating the object for manual database to input the selected values in the database by the user
         */
        var manual = Manual()
        manual.Type = type
        manual.Input = input
        manual.Duration = manualViewModel.duration.value!!
        manual.Distance = manualViewModel.distance.value!!
        if(date.isEmpty())
        {
            val time = Calendar.getInstance().time
            val formatter = SimpleDateFormat.getDateInstance()
            date = formatter.format(time)
        }
        manual.Date = date
        if(time.isEmpty())
        {

            var hour = calendar.get(Calendar.HOUR_OF_DAY)
            var minutes = calendar.get(Calendar.MINUTE)
            var seconds = calendar.get(Calendar.SECOND)
            time = "$hour:$minutes:$seconds"
        }
        manual.Time = time
        manual.Heart_Rate = manualViewModel.heartRate.value!!
        manual.Calories = manualViewModel.calories.value!!
        manual.Comment = manualViewModel.comment.value!!
        manual.locations = manualViewModel.locations.value!!
        manualViewModel.insert(manual)
        if(manualViewModel.allManualsLiveData.value?.isEmpty() == true)
            Toast.makeText(this, "Entry #0 saved.", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(this, "Entry #${manualViewModel.allManualsLiveData.value?.last()?.id} saved.", Toast.LENGTH_SHORT).show()
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        /*
        checking which month is chosen by the user
         */
        var month = ""
        if(p2 == 1)
            month = "Jan"
        else if(p2 == 2)
            month = "Feb"
        else if(p2 == 3)
            month = "Mar"
        else if(p2 == 4)
            month = "Apr"
        else if(p2 == 5)
            month = "May"
        else if(p2 == 6)
            month = "Jun"
        else if(p2 == 7)
            month = "Jul"
        else if(p2 == 8)
            month = "Aug"
        else if(p2 == 9)
            month = "Sep"
        else if(p2 == 10)
            month = "Oct"
        else if(p2 == 11)
            month = "Nov"
        else
            month = "Dec"

        date = "$p3 $month, $p1"

    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {

        time = "$p1:$p2:${calendar.get(Calendar.SECOND)}"
    }
}