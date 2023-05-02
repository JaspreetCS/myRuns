package com.example.myruns4

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences

class MyListAdapter(private val context: Context, private var manualList: List<Manual>) : BaseAdapter(){

    override fun getItem(position: Int): Any {
        return manualList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return manualList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.layout_adapter,null)

        val textViewID = view.findViewById(R.id.header) as TextView
        val textViewComment = view.findViewById(R.id.activity_type) as TextView

        if(manualList.get(position).Input == "Manual Entry")
        {
            //Checking which unit is selected by the user
            val currValue = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("pref_units", "Metric (Kilometers)")
            var unit = ""
            var distance: Double = 0.0

            if(currValue == "Metric (Kilometers)")
            {
                unit = "Kilometers"
            }
            else {
                unit = "Miles"
            }
            if(unit == "Kilometers")
            {
                distance = manualList.get(position).Distance*1.61
            }
            else
            {
                distance = manualList.get(position).Distance
            }
            textViewID.text = "${manualList.get(position).Input}: ${manualList.get(position).Type}, ${manualList.get(position).Time}, ${manualList.get(position).Date}"
            textViewComment.text = "$distance $unit, ${manualList.get(position).Duration} secs"
        }

        else if(manualList.get(position).Type == "GPS")
        {
            //Checking which unit is selected by the user
            val currValue = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("pref_units", "Metric (Kilometers)")
            var unit = ""
            var distance: Double = 0.0

            if(currValue == "Metric (Kilometers)")
            {
                unit = "Kilometers"
            }
            else {
                unit = "Miles"
            }
            if(unit == "Kilometers")
            {
                distance = manualList.get(position).Distance*1.61
            }
            else
            {
                distance = manualList.get(position).Distance
            }
            textViewID.text = "${manualList.get(position).Type}: ${manualList.get(position).Input}, ${manualList.get(position).Time}, ${manualList.get(position).Date}"
            textViewComment.text = "${String.format("%.3f" ,distance)} $unit, ${manualList.get(position).Duration} secs"

        }
        else
        {
            //Checking which unit is selected by the user
            val currValue = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("pref_units", "Metric (Kilometers)")
            var unit = ""
            var distance: Double = 0.0

            if(currValue == "Metric (Kilometers)")
            {
                unit = "Kilometers"
            }
            else {
                unit = "Miles"
            }
            if(unit == "Kilometers")
            {
                distance = manualList.get(position).Distance*1.61
            }
            else
            {
                distance = manualList.get(position).Distance
            }
            textViewID.text = "${manualList.get(position).Type}: ${manualList.get(position).Input}, ${manualList.get(position).Time}, ${manualList.get(position).Date}"
            textViewComment.text = "${String.format("%.3f" ,distance)} $unit, ${manualList.get(position).Duration} secs"
        }


        return view
    }

    fun replace(newCommentList: List<Manual>){
        manualList = newCommentList
    }


}