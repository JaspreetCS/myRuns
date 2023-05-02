package com.example.myruns4

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider


class Start : Fragment() {
    private lateinit var start: Button
    private lateinit var inputSpinner: Spinner
    private lateinit var activitySpinner: Spinner
    private lateinit var value: String
    var value2 = ""

    private lateinit var startActivity: ActivityResultLauncher<Intent>

    private lateinit var inputTypeSpinner : Spinner
    private lateinit var activityTypeSpinner : Spinner
    private lateinit var startButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View?
    {
        val view = inflater.inflate(R.layout.start, container, false)

        Util.checkPermissions(requireActivity())
        val inputValue = resources.getStringArray(R.array.inputType)
        inputSpinner = view.findViewById(R.id.inputSpinner)

        if(inputSpinner != null) {
            val adapter = ArrayAdapter<Any?>(requireActivity(), R.layout.spinner_list, inputValue)
            adapter.setDropDownViewResource(R.layout.spinner_list)
            inputSpinner.adapter = adapter

            inputSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long,
                ) {

                        value = parent.getItemAtPosition(position) as String
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }

        }

        val activityValue = resources.getStringArray(R.array.activityType)
        activitySpinner = view.findViewById(R.id.activitySpinner)

        if(activitySpinner != null) {
            val adapter = ArrayAdapter<Any?>(requireActivity(), R.layout.spinner_list, activityValue)
            adapter.setDropDownViewResource(R.layout.spinner_list)
            activitySpinner.adapter = adapter

            activitySpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?, position: Int, id: Long,
                ) {
                    value2 = parent.getItemAtPosition(position) as String
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }


        start = view.findViewById<Button>(R.id.startButton)
        start.setOnClickListener {
            if(value == "Manual Entry") {
                val intent = Intent(requireActivity(), Start_Button::class.java)
                val bundle = Bundle()
                bundle.putString("entry_type",value)
                bundle.putString("input_type",value2)
                intent.putExtras(bundle)
                startActivity(intent)
            }
            else{
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                val bundle = Bundle()
                bundle.putString("entry_type",value)
                bundle.putString("input_type",value2)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
        return view
    }

    }
