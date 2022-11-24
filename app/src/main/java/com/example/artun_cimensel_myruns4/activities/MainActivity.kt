package com.example.artun_cimensel_myruns4.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.Util
import com.example.artun_cimensel_myruns4.adapters.FragmentStateAdapter
import com.example.artun_cimensel_myruns4.fragments.HistoryFragment
import com.example.artun_cimensel_myruns4.fragments.SettingsFragment
import com.example.artun_cimensel_myruns4.fragments.StartFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var myMyFragmentStateAdapter: FragmentStateAdapter
    private lateinit var fragments: ArrayList<Fragment>
    private val tabTitles = arrayOf("Start", "History", "Settings") //Tab titles
    private lateinit var tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Util.checkPermissions(this)

        viewPager2 = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabChanger)

        fragments = ArrayList()
        fragments.add(StartFragment())
        fragments.add(HistoryFragment())
        fragments.add(SettingsFragment())

        myMyFragmentStateAdapter = FragmentStateAdapter(this, fragments)
        viewPager2.adapter = myMyFragmentStateAdapter

        tabConfigurationStrategy =
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = tabTitles[position]
            }
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}