package com.example.artun_cimensel_myruns4.adapters

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.activities.UserProfileActivity
import com.example.artun_cimensel_myruns4.data.CustomSetting
import com.example.artun_cimensel_myruns4.fragments.SettingsDialog
import com.example.artun_cimensel_myruns4.fragments.SettingsFragment

class SettingsListAdapter(private val activity: FragmentActivity, private val list: Array<CustomSetting>) : BaseAdapter() {
    private lateinit var v: View
    private lateinit var sPrefs: SharedPreferences

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(activity) as LayoutInflater
        v = inflater.inflate(R.layout.custom_list_item_2_checkable, null)
        sPrefs = activity.getSharedPreferences("SettingsTabPrefs", AppCompatActivity.MODE_PRIVATE)

        val pText = v.findViewById<TextView>(R.id.primary_text)
        val sText = v.findViewById<TextView>(R.id.secondary_text)
        val checkBox = v.findViewById<CheckBox>(R.id.checkbox_settings)

        pText.text = list[position].primaryText
        sText.text = list[position].secondaryText

        if(list[position].checkboxEnabled){
            checkBox.visibility = View.VISIBLE
            when (sPrefs.getBoolean("PrivacyChecked", false)) {
                true -> checkBox.isChecked = true
                false -> checkBox.isChecked = false
            }

            checkBox.setOnCheckedChangeListener{ _, isChecked ->
                val editor = sPrefs.edit()
                editor.putBoolean("PrivacyChecked", isChecked)
                editor.apply()
            }
        }
        else checkBox.visibility = View.GONE

        v.setOnClickListener{
            when (list[position].settingType) {
                SettingsFragment.MY_RUNS_1 -> {
                    val intent = Intent(activity, UserProfileActivity::class.java)
                    activity.startActivity(intent)
                }
                SettingsFragment.CHECKBOX -> {
                    checkBox.isChecked = !checkBox.isChecked
                }
                SettingsFragment.RADIO_BUTTONS -> {
                    val dialog = SettingsDialog()
                    val bundle = Bundle()

                    bundle.putInt(SettingsDialog.KEY_IDX, list[position].settingType)
                    bundle.putString(SettingsDialog.KEY_TITLE, pText.text.toString())

                    dialog.arguments = bundle
                    dialog.show(activity.supportFragmentManager, "SettingsDialog")
                }
                SettingsFragment.EDIT_TEXT -> {
                    val dialog = SettingsDialog()
                    val bundle = Bundle()

                    bundle.putInt(SettingsDialog.KEY_IDX, list[position].settingType)
                    bundle.putString(SettingsDialog.KEY_TITLE, pText.text.toString())

                    dialog.arguments = bundle
                    dialog.show(activity.supportFragmentManager, "SettingsDialog")
                }
                SettingsFragment.LINK -> {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sText.text.toString()))
                    activity.startActivity(intent)
                }
            }
        }

        return v
    }
}