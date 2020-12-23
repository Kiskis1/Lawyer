package com.acruxcs.lawyer.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.acruxcs.lawyer.MainActivity
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentWorkingHoursBinding
import com.acruxcs.lawyer.model.WorkingHours
import com.crazylegend.viewbinding.viewBinding
import zion830.com.range_picker_dialog.TimeRangePickerDialog

class WorkingHoursFragment : Fragment(R.layout.fragment_working_hours) {
    private val binding by viewBinding(FragmentWorkingHoursBinding::bind)
    private val viewModel: ProfileViewModel by viewModels({ requireParentFragment() })
    private var hours = MainActivity.user.value!!.workingHours

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLayouts()
        setupTextViews()
        with(binding) {
            toolbar.toolbar.apply {
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
                setTitle(R.string.dialog_title_working_hours)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_confirm -> {
                            saveHours()
                            viewModel.updateHours(hours!!)
                            findNavController().navigateUp()
                            true
                        }
                        else -> false
                    }
                }
            }
        }
    }

    private fun setupTextViews() {
        with(binding) {
            textMondayTime.text = hours?.monday
            textTuesdayTime.text = hours?.tuesday
            textWednesdayTime.text = hours?.wednesday
            textThursdayTime.text = hours?.thursday
            textFridayTime.text = hours?.friday
            textSaturdayTime.text = hours?.saturday
            textSundayTime.text = hours?.sunday
        }
    }

    private fun saveHours() {
        if (hours == null) hours = WorkingHours()
        with(binding) {
            hours!!.monday = textMondayTime.text.toString()
            hours!!.tuesday = textTuesdayTime.text.toString()
            hours!!.wednesday = textWednesdayTime.text.toString()
            hours!!.thursday = textThursdayTime.text.toString()
            hours!!.friday = textFridayTime.text.toString()
            hours!!.saturday = textSaturdayTime.text.toString()
            hours!!.sunday = textSundayTime.text.toString()
        }
    }

    private fun showDialog(tv: TextView) {
        TimeRangePickerDialog.Builder()
            .setTimeRange(8, 0, 17, 0)
            .setOnTimeRangeSelectedListener { timeRange ->
                tv.text = timeRange.readableTimeRange
            }
            .build()
            .show(parentFragmentManager)
    }

    private fun setupLayouts() {
        with(binding) {
            layoutMonday.setOnClickListener {
                showDialog(textMondayTime)
            }
            layoutTuesday.setOnClickListener {
                showDialog(textTuesdayTime)
            }
            layoutWednesday.setOnClickListener {
                showDialog(textWednesdayTime)
            }
            layoutThursday.setOnClickListener {
                showDialog(textThursdayTime)
            }
            layoutFriday.setOnClickListener {
                showDialog(textFridayTime)
            }
            layoutSaturday.setOnClickListener {
                showDialog(textSaturdayTime)
            }
            layoutSunday.setOnClickListener {
                showDialog(textSundayTime)
            }

        }
    }

    fun resetTime(v: View) {
        with(binding) {
            when (v.id) {
                R.id.text_monday_reset -> {
                    textMondayTime.text = ""
                }
                R.id.text_tuesday_reset -> {
                    textTuesdayTime.text = ""
                }
                R.id.text_wednesday_reset -> {
                    textWednesdayTime.text = ""
                }
                R.id.text_thursday_reset -> {
                    textThursdayTime.text = ""
                }
                R.id.text_friday_reset -> {
                    textFridayTime.text = ""
                }
                R.id.text_saturday_reset -> {
                    textSaturdayTime.text = ""
                }
                R.id.text_sunday_reset -> {
                    textSundayTime.text = ""
                }
            }
        }
    }
}
