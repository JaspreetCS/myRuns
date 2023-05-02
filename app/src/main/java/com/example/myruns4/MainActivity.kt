package com.example.myruns4

import TabPageAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.myruns4.History
import com.example.myruns4.Settings
import com.example.myruns4.Start
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class MainActivity : AppCompatActivity() {
    private lateinit var start: Start
    private lateinit var history: History
    private lateinit var settings: Settings
    private lateinit var tabs: ArrayList<Fragment>
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabPageAdapter: TabPageAdapter
    private val tabTitles = arrayOf("START","HISTORY","SETTINGS")
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        start = Start()
        history= History()
        settings = Settings()
        tabs = ArrayList()
        tabs.add(start)
        tabs.add(history)
        tabs.add(settings)

        tabLayout = findViewById(R.id.tab)
        viewPager2 = findViewById(R.id.viewPager)

        tabPageAdapter = TabPageAdapter(this, tabs)
        viewPager2.adapter = tabPageAdapter

        tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy(){
                tab: TabLayout.Tab, position: Int ->
            tab.text =  tabTitles[position]
        }

        tabLayoutMediator = TabLayoutMediator(tabLayout,viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()

    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}