package com.example.myruns4

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import java.time.Duration


class InputDialog : DialogFragment(), DialogInterface.OnClickListener {
    companion object {
        const val DIALOG_KEY = "DIALOG"
        const val DURATION = 1
        const val DISTANCE = 2
        const val CALORIES = 3
        const val HEART_RATE = 4
        const val COMMENT = 5
    }

    private lateinit var editText: EditText
    private var text: String = ""

    private var dialogId = 0
    private var entityId:Long = 0

    private lateinit var database: ManualDatabase
    private lateinit var databaseDao: ManualDatabaseDao
    private lateinit var repository: ManualRepository
    private lateinit var viewModelFactory: ManualViewModelFactory
    private lateinit var manualViewModel: ManualViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog : Dialog
        val bundle = arguments
        dialogId = bundle!!.getInt(DIALOG_KEY)

        database = ManualDatabase.getInstance(requireActivity())
        databaseDao = database.manualDatabaseDao
        repository = ManualRepository(databaseDao)
        viewModelFactory = ManualViewModelFactory(repository)
        manualViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(ManualViewModel::class.java)
//
//        entityId = manualViewModel.getID("*").toLong()

        val builder = AlertDialog.Builder(requireActivity())
        val view: View = requireActivity().layoutInflater.inflate(R.layout.input_dialog, null)
        builder.setView(view)
        editText = view.findViewById<EditText>(R.id.editText)
        if (dialogId == DURATION) {
            builder.setTitle("Duration")
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        else if (dialogId == DISTANCE) {
            builder.setTitle("Distance")
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        else if (dialogId == CALORIES) {
            builder.setTitle("Calories")
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        else if (dialogId == HEART_RATE) {
            builder.setTitle("Heart Rate")
            editText.inputType = InputType.TYPE_CLASS_NUMBER
        }
        else {
            builder.setTitle("Comments")
            editText.hint = "How did it go? Notes here."
        }
        builder.setPositiveButton("ok",this)
        builder.setNegativeButton("cancel", this)
        dialog = builder.create()
        return dialog

        }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            if (!editText.text.isEmpty()) {
                text = editText.text.toString()
                if (dialogId == DURATION)
                    manualViewModel.duration.value = text.toDouble()
                else if (dialogId == DISTANCE)
                    manualViewModel.distance.value = text.toDouble()
                else if (dialogId == HEART_RATE)
                    manualViewModel.heartRate.value = text.toInt()
                else if (dialogId == CALORIES)
                    manualViewModel.calories.value = text.toDouble()
                else if (dialogId == COMMENT)
                    manualViewModel.comment.value = text
            }
            else
            {
                if (dialogId == DURATION)
                    manualViewModel.duration.value = 0.0
                else if (dialogId == DISTANCE)
                    manualViewModel.distance.value = 0.0
                else if (dialogId == HEART_RATE)
                    manualViewModel.heartRate.value = 0
                else if (dialogId == CALORIES)
                    manualViewModel.calories.value = 0.0
                else if (dialogId == COMMENT)
                    manualViewModel.comment.value = ""
            }

        }
        else if (which == DialogInterface.BUTTON_NEGATIVE)
            Toast.makeText(requireActivity(),"Cancel",Toast.LENGTH_SHORT).show()
    }

}