package com.example.artun_cimensel_myruns4.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.example.artun_cimensel_myruns4.R
import com.example.artun_cimensel_myruns4.activities.ManualEntriesActivity
import java.util.*
import kotlin.properties.Delegates


class ManualEntriesDialog : DialogFragment(), DialogInterface.OnClickListener,
    TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener{
    companion object{
        const val KEY_IDX = "index"
        const val KEY_TITLE = "title"
    }

    private var mIndex by Delegates.notNull<Int>()
    private lateinit var mEditText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        val index = bundle?.getInt(KEY_IDX)
        val title = bundle?.getString(KEY_TITLE) as String

        mIndex = index!!

        return when (index) {
            0, 1 -> returnDateTimeDialogs(index)
            2, 3, 4, 5, 6 -> returnOtherDialogs(index, title)
            else -> AlertDialog.Builder(requireActivity()).create()
        }
    }

    private fun returnDateTimeDialogs(index: Int): Dialog {
        val calendar = Calendar.getInstance()

        return when (index) {
            0 -> DatePickerDialog(requireActivity(), this, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
            1 -> TimePickerDialog(requireActivity(), this, calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), false)
            else -> AlertDialog.Builder(requireActivity()).create()
        }
    }

    private fun returnOtherDialogs(index: Int, title: String): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view: View = requireActivity().layoutInflater.inflate(R.layout.dialog_single_edittext, null)
        val editText = view.findViewById<EditText>(R.id.input_editText)

        mEditText = editText

        when (index) {
            2, 3 -> editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            4, 5 -> editText.inputType = InputType.TYPE_CLASS_NUMBER
            6 -> {
                editText.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                editText.setHint(R.string.how_did_it_go_notes_here)
            }
        }

        builder.setTitle(title)
        builder.setView(view)
        builder.setPositiveButton("ok", this)
        builder.setNegativeButton("cancel", this)

        return builder.create()
    }

    override fun onClick(dialog: DialogInterface, item: Int) {
        if (item == DialogInterface.BUTTON_POSITIVE) {
            when (mIndex) {
                2, 3, 4, 5, 6 -> (activity as ManualEntriesActivity).setOther(mIndex, mEditText.text.toString())
            }
        }
//        else if (item == DialogInterface.BUTTON_NEGATIVE) {}
        dismiss()
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        (activity as ManualEntriesActivity).setDate(year, month, day)
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        (activity as ManualEntriesActivity).setTime(hour, minute)
    }
}