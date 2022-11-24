package com.example.artun_cimensel_myruns4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.artun_cimensel_myruns4.data.CustomSetting
import com.example.artun_cimensel_myruns4.adapters.SettingsListAdapter
import com.example.artun_cimensel_myruns4.R

class SettingsFragment : Fragment() {
    companion object{
        const val MY_RUNS_1 = 0
        const val CHECKBOX = 1
        const val RADIO_BUTTONS = 2
        const val EDIT_TEXT = 3
        const val LINK = 4
    }

    private val accountPreferences = arrayOf(
        CustomSetting("Name, Email, Class, etc", "User Profile", false, MY_RUNS_1),
        CustomSetting("Privacy Setting", "Posting your records anonymously", true, CHECKBOX)
    )
    private val additionalSettings = arrayOf(
        CustomSetting("Unit Preference", "Select the units", false, RADIO_BUTTONS),
        CustomSetting("Comments", "Please enter your comments", false, EDIT_TEXT)
    )
    private val miscEntries = arrayOf(CustomSetting("Webpage", "https://www.sfu.ca/computing.html", false, LINK))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accPrefsList = view.findViewById<ListView>(R.id.settingsTab_accountPrefsList)
        val addSetList = view.findViewById<ListView>(R.id.settingsTab_additionalSettingsList)
        val miscList = view.findViewById<ListView>(R.id.settingsTab_miscList)

        accPrefsList.adapter = SettingsListAdapter(requireActivity(), accountPreferences)
        addSetList.adapter = SettingsListAdapter(requireActivity(), additionalSettings)
        miscList.adapter = SettingsListAdapter(requireActivity(), miscEntries)
    }
}