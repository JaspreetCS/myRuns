package com.example.myruns4

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ChangeDialog: DialogFragment(), DialogInterface.OnClickListener {

    lateinit var textView : TextView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog
        val builder = AlertDialog.Builder(requireActivity())
        val view: View = requireActivity().layoutInflater.inflate(R.layout.changedialog, null)
        builder.setView(view)
        builder.setTitle("Pick Profile Picture")
        dialog = builder.create()
        return dialog
    }

    override fun onClick(p0: DialogInterface?, p1: Int) {

    }
}