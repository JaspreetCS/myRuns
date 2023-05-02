package com.example.myruns4

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


class History : Fragment() {

    private lateinit var arrayList: ArrayList<Manual>
    private lateinit var arrayAdapter: MyListAdapter
    private lateinit var myListView: ListView

    private lateinit var database: ManualDatabase
    private lateinit var databaseDao: ManualDatabaseDao
    private lateinit var repository: ManualRepository
    private lateinit var viewModelFactory: ManualViewModelFactory
    private lateinit var manualViewModel: ManualViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?
    {
        val view = inflater.inflate(R.layout.history, container, false)


        myListView = view.findViewById(R.id.database_listview)
        arrayList = ArrayList()
        arrayAdapter = MyListAdapter(requireActivity(), arrayList)
        myListView.adapter = arrayAdapter

        database = ManualDatabase.getInstance(requireActivity())
        databaseDao = database.manualDatabaseDao
        repository = ManualRepository(databaseDao)
        viewModelFactory = ManualViewModelFactory(repository)
        manualViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(ManualViewModel::class.java)

        manualViewModel.allManualsLiveData.observe(requireActivity(), Observer { it ->
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })

        arrayList = ArrayList()
        arrayAdapter = MyListAdapter(requireActivity(), arrayList)
        myListView.adapter = arrayAdapter


        myListView.setOnItemClickListener { parent, view, position, id ->

            val thread2 = Thread(){
                val runnable = kotlinx.coroutines.Runnable() {
                    readEntry(position)
                }
                val handler = Handler(Looper.getMainLooper())
                handler.post(runnable)
            }
            thread2.start()

        }
        return view
    }

    fun readEntry(position:Int)
    {
        //Sending the database entries to Display the entry to user
        if(manualViewModel.allManualsLiveData.value?.get(position)!!.Input == "Manual Entry")
        {
            val intent = Intent(requireActivity(), EntryView::class.java)
            intent.putExtra("id",(manualViewModel.allManualsLiveData.value?.get(position)!!.id))
            intent.putExtra("input",(manualViewModel.allManualsLiveData.value?.get(position)!!.Input))
            intent.putExtra("type",(manualViewModel.allManualsLiveData.value?.get(position)!!.Type))
            intent.putExtra("distance",(manualViewModel.allManualsLiveData.value?.get(position)!!.Distance).toString())
            intent.putExtra("duration",(manualViewModel.allManualsLiveData.value?.get(position)!!.Duration).toString())
            intent.putExtra("calories",(manualViewModel.allManualsLiveData.value?.get(position)!!.Calories).toString())
            intent.putExtra("time",(manualViewModel.allManualsLiveData.value?.get(position)!!.Time))
            intent.putExtra("date", (manualViewModel.allManualsLiveData.value?.get(position)!!.Date))
            intent.putExtra("heart_rate",(manualViewModel.allManualsLiveData.value?.get(position)!!.Heart_Rate).toString())
            intent.putExtra("comment",(manualViewModel.allManualsLiveData.value?.get(position)!!.Comment))
            intent.putExtra("locations",(manualViewModel.allManualsLiveData.value?.get(position)!!.locations))
            startActivity(intent)
        }
        else
        {
            val intent = Intent(requireActivity(), MapsViewActivity::class.java)
            intent.putExtra("id",(manualViewModel.allManualsLiveData.value?.get(position)!!.id))
            intent.putExtra("input",(manualViewModel.allManualsLiveData.value?.get(position)!!.Input))
            intent.putExtra("type",(manualViewModel.allManualsLiveData.value?.get(position)!!.Type))
            intent.putExtra("distance",(manualViewModel.allManualsLiveData.value?.get(position)!!.Distance).toString())
            intent.putExtra("duration",(manualViewModel.allManualsLiveData.value?.get(position)!!.Duration).toString())
            intent.putExtra("calories",(manualViewModel.allManualsLiveData.value?.get(position)!!.Calories).toString())
            intent.putExtra("climb",(manualViewModel.allManualsLiveData.value?.get(position)!!.Climb).toString())
            intent.putExtra("avg_speed",(manualViewModel.allManualsLiveData.value?.get(position)!!.Avg_Speed))
            intent.putExtra("avg_pace",(manualViewModel.allManualsLiveData.value?.get(position)!!.Avg_Pace))

            intent.putExtra("locations",fromArrayList(manualViewModel.allManualsLiveData.value?.get(position)!!.locations))
            startActivity(intent)
        }
    }
    fun fromArrayList(array: ArrayList<LatLng>?): String
    {
        return Gson().toJson(array)
    }

    override fun onResume() {
        super.onResume()
        manualViewModel.allManualsLiveData.observe(requireActivity(), Observer { it ->
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })
    }
}