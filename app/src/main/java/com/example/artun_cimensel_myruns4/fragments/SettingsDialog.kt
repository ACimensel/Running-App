package com.example.artun_cimensel_myruns4.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.artun_cimensel_myruns4.R

class SettingsDialog : DialogFragment(), DialogInterface.OnClickListener {

    companion object{
        const val KEY_IDX = "index"
        const val KEY_TITLE = "title"
    }

    private lateinit var v: View
    private lateinit var sPrefs: SharedPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val type = bundle?.getInt(KEY_IDX)
        val title = bundle?.getString(KEY_TITLE) as String
        val builder = AlertDialog.Builder(requireActivity())
        sPrefs = requireActivity().getSharedPreferences("SettingsDialogPrefs", AppCompatActivity.MODE_PRIVATE)

        when (type) {
            SettingsFragment.RADIO_BUTTONS -> {
                v = requireActivity().layoutInflater.inflate(R.layout.dialog_radiogroup_2, null)
                val radioGroup = v.findViewById<RadioGroup>(R.id.dialog_radioGroup)
                val editor = sPrefs.edit()

                when (sPrefs.getInt("UnitBtnId", R.id.metric)) {
                    R.id.metric -> radioGroup.check(R.id.metric)
                    R.id.imperial -> radioGroup.check(R.id.imperial)
                    else -> radioGroup.clearCheck()
                }

                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                    editor.putInt("UnitBtnId", checkedId)
                    editor.apply()
                }
            }

            SettingsFragment.EDIT_TEXT -> {
                v = requireActivity().layoutInflater.inflate(R.layout.dialog_single_edittext, null)
                builder.setPositiveButton("ok", this)

                val commentText = v.findViewById<EditText>(R.id.input_editText)
                commentText.setText(sPrefs.getString("Comment", ""))
            }
        }

        builder.setTitle(title)
        builder.setView(v)
        builder.setNegativeButton("cancel", this)

        return builder.create()
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if(item == DialogInterface.BUTTON_POSITIVE) {
            val editor = sPrefs.edit() as SharedPreferences.Editor
            editor.putString("Comment", v.findViewById<EditText>(R.id.input_editText).text.toString())
            editor.apply()
        }
    }
}