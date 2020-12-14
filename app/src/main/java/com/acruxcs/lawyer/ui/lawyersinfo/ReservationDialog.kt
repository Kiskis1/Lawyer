package com.acruxcs.lawyer.ui.lawyersinfo

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.DialogReservationBinding
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.utils.Utils
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.vivekkaushik.datepicker.OnDateSelectedListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ReservationDialog(private val viewModel: LawyersViewModel) : DialogFragment(),
    TimeSelectionAdapter.Interaction {
    private lateinit var binding: DialogReservationBinding
    private lateinit var lawyer: User
    private lateinit var reservation: Reservation
    private var selectedDate: LocalDate? = null
    private var selectedTime: LocalTime? = null

    private val timeAdapter by lazy { TimeSelectionAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(Utils.ARG_LAWYER)!!
            reservation = Reservation(lawyer = lawyer)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogReservationBinding.inflate(LayoutInflater.from(requireContext()))
        reservation.user = MainApplication.user.value!!.uid
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity, R.style.DialogTheme)

            with(binding) {
                recyclerView.adapter = timeAdapter
                toolbar.toolbar.apply {
                    setTitle(R.string.dialog_title_reservation)
                    setNavigationOnClickListener {
                        dismiss()
                    }
                }
                lawyer.workingHours?.let {
                    toolbar.toolbar.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.action_confirm -> {
                                selectedDate ?: run {
                                    shortToast(R.string.error_select_date)
                                    return@setOnMenuItemClickListener true
                                }
                                selectedTime ?: run {
                                    shortToast(R.string.error_select_time)
                                    return@setOnMenuItemClickListener true
                                }
                                reservation.date = "$selectedDate"
                                reservation.time = "$selectedTime"
                                reservation.dateLawyer = "$selectedDate" + "_" + lawyer.uid
                                viewModel.createReservation(reservation)
                                dismiss()
                                true
                            }
                            else -> false
                        }
                    }
                    dayPicker.apply {
                        setInitialDate(
                            LocalDate.now().year,
                            LocalDate.now().month.value - 1,
                            LocalDate.now().dayOfMonth
                        )
                        setOnDateSelectedListener(object : OnDateSelectedListener {
                            override fun onDateSelected(
                                year: Int,
                                month: Int,
                                day: Int,
                                dayOfWeek: Int,
                            ) {
                                val str = String.format("$year-%02d-%02d", month + 1, day)
                                selectedDate =
                                    LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                selectedTime = null
                                timeAdapter.resetSelected()
                                viewModel.getAvailableTimes(str, dayOfWeek, lawyer)
                                    .observe(this@ReservationDialog, {
                                        timeAdapter.swapData(it)
                                    })
                            }

                            override fun onDisabledDateSelected(
                                year: Int,
                                month: Int,
                                day: Int,
                                dayOfWeek: Int,
                                isDisabled: Boolean,
                            ) = Unit
                        })

                    }
                } ?: run {
                    toolbar.toolbar.menu.findItem(R.id.action_confirm).isVisible = false
                    textEmptyList.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    dayPicker.visibility = View.GONE
                }
            }

            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        fun newInstance(lawyer: User, viewModel: LawyersViewModel) =
            ReservationDialog(viewModel).apply {
                arguments = Bundle().apply {
                    putParcelable(Utils.ARG_LAWYER, lawyer)
                }
            }
    }

    override fun onTimeSelected(time: LocalTime?) {
        time?.let { selectedTime = it } ?: run { return }
    }
}
