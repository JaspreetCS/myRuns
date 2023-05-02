package com.example.myruns4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceManager.getDefaultSharedPreferences


class Settings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        childFragmentManager.beginTransaction()
            .replace(R.id.idFrameLayout, SettingsFragment()).commit()


        return inflater.inflate(R.layout.settings, container, false)
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        override fun onActivityCreated(savedInstanceState: Bundle?) {
            super.onActivityCreated(savedInstanceState)

        }

        private lateinit var database: ManualDatabase
        private lateinit var databaseDao: ManualDatabaseDao
        private lateinit var repository: ManualRepository
        private lateinit var viewModelFactory: ManualViewModelFactory
        private lateinit var manualViewModel: ManualViewModel

        private lateinit var myViewModel: MyViewModel
        override fun onCreatePreferences(bundle: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

        }

    }


}

