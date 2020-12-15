package com.acruxcs.lawyer.ui.lawyersinfo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.acruxcs.lawyer.MainApplication
import com.acruxcs.lawyer.R
import com.acruxcs.lawyer.databinding.FragmentNewReservationBinding
import com.acruxcs.lawyer.model.Reservation
import com.acruxcs.lawyer.model.User
import com.acruxcs.lawyer.ui.lawyers.LawyersViewModel
import com.acruxcs.lawyer.utils.Utils
import com.crazylegend.kotlinextensions.fragments.shortToast
import com.crazylegend.viewbinding.viewBinding
import com.vivekkaushik.datepicker.OnDateSelectedListener
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NewReservationFragment : Fragment(R.layout.fragment_new_reservation),
    TimeSelectionAdapter.Interaction {
    private var lawyer: User? = null
    private var reservation: Reservation? = null
    private var selectedDate: LocalDate? = null
    private var selectedTime: LocalTime? = null
    private val binding by viewBinding(FragmentNewReservationBinding::bind)
    private val viewModel: LawyersViewModel by viewModels()
    private var tagas: String? = null

    private val timeAdapter by lazy { TimeSelectionAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lawyer = it.getParcelable(Utils.ARG_LAWYER)
            tagas = it.getString("tag")
            reservation = it.getParcelable("reservation")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (lawyer == null)
            lawyer = reservation!!.lawyer
        with(binding) {
            recyclerView.adapter = timeAdapter
            toolbar.toolbar.apply {
                setTitle(R.string.dialog_title_reservation)
                setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }
            lawyer!!.workingHours?.let {
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
                            if (tagas != null && tagas == "edit_reservation") {
                                reservation!!.date = "$selectedDate"
                                reservation!!.time = "$selectedTime"
                                reservation!!.dateLawyer = "$selectedDate" + "_" + lawyer!!.uid
                            } else {
                                reservation = Reservation(lawyer = lawyer)
                                reservation!!.user = MainApplication.user.value!!.uid
                                reservation!!.date = "$selectedDate"
                                reservation!!.time = "$selectedTime"
                                reservation!!.dateLawyer = "$selectedDate" + "_" + lawyer!!.uid
                            }
                            viewModel.createReservation(reservation!!)
                            findNavController().navigateUp()
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
                            viewModel.getAvailableTimes(str, dayOfWeek, lawyer!!)
                                .observe(viewLifecycleOwner, {
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
    }

    override fun onTimeSelected(time: LocalTime?) {
        time?.let { selectedTime = it } ?: run { return }
    }
}
